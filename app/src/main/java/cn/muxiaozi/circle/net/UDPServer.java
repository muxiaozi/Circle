package cn.muxiaozi.circle.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import cn.muxiaozi.circle.core.IConfig;
import cn.muxiaozi.circle.core.SystemPacket;
import cn.muxiaozi.circle.room.UserBean;

/**
 * Created by 慕宵子 on 2016/7/17.
 * <p/>
 * UDP服务端
 */
public class UDPServer implements Runnable, ISocket {

    /**
     * Service回调
     */
    private IDataService mListener;

    private DatagramSocket mServerSocket;
    private DatagramPacket mReceiverPacket;

    private DataHandle mDataHandle;

    private boolean isRunning;

    UDPServer(IDataService listener) {
        mListener = listener;
        mReceiverPacket = new DatagramPacket(new byte[512], 512);

        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            mServerSocket = new DatagramSocket(IConfig.LOCAL_PORT);

            //开始心跳
            mDataHandle = new DataHandle(mServerSocket);
            mDataHandle.setPriority(Thread.MAX_PRIORITY);
            mDataHandle.start();

            while (isRunning) {
                mServerSocket.receive(mReceiverPacket);
                mDataHandle.receive(mReceiverPacket);
            }
        } catch (IOException e) {
            close();
            e.printStackTrace();
        }
    }

    /**
     * 心跳检测
     */
    private class DataHandle extends Thread {

        //心跳间隔(ms)
        private static final int INTERVAL_HEART_BEAT = 1000;

        //客户端列表
        private ArrayList<ClientEntity> mClients;

        //发送数据队列
        private BlockingQueue<byte[]> sendQueue;

        //接收数据队列
        private BlockingQueue<DatagramPacket> receiveQueue;

        //服务端Socket
        private DatagramSocket mSocket;

        //临时数据包
        private byte[] tmpData;
        private DatagramPacket tmpPacket;

        DataHandle(DatagramSocket socket) {
            mSocket = socket;

            mClients = new ArrayList<>(IConfig.MAX_CLIENT_NUM);
            sendQueue = new ArrayBlockingQueue<>(5);
            receiveQueue = new ArrayBlockingQueue<>(5);
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();

            while (isRunning) {
                /**
                 * 思路：
                 * 先把每个客户端计数+1，并同时向它发送一条心跳消息
                 * 如果接受到该客户端心跳反馈，则再-1
                 */
                if (System.currentTimeMillis() - startTime > INTERVAL_HEART_BEAT) {
                    sendExceptOne(SystemPacket.packHeartBeat(), null);

                    Iterator<ClientEntity> iterator = mClients.iterator();
                    while (iterator.hasNext()) {
                        ClientEntity entity = iterator.next();
                        if (entity.noReplyCount++ > 2) {
                            mListener.receive(SystemPacket.packFriendOut(entity.imei));
                            sendExceptOne(SystemPacket.packFriendOut(entity.imei), entity.address);
                            iterator.remove();
                        }
                    }
                    startTime = System.currentTimeMillis();
                }

                //处理发送队列
                if ((tmpData = sendQueue.poll()) != null && mSocket != null) {
                    sendExceptOne(tmpData, null);
                }

                //处理接受队列
                if ((tmpPacket = receiveQueue.poll()) != null) {
                    tmpData = tmpPacket.getData();

                    if (tmpData[0] == SystemPacket.TYPE_HEART_BEAT) {
                        for (ClientEntity entity : mClients) {
                            if (entity.address.equals(tmpPacket.getAddress())) {
                                entity.noReplyCount--;
                                break;
                            }
                        }
                    } else {
                        if (tmpData[0] == SystemPacket.TYPE_FRIEND_IN) {
                            mListener.getOnlineUserInfo(new IDataService.IGetUserInfo() {
                                @Override
                                public void onGet(UserBean bean) {
                                    sendToOne(SystemPacket.packFriendIn(bean), tmpPacket.getAddress());
                                }
                            });

                            mClients.add(new ClientEntity(
                                    SystemPacket.unpackFriendIn(tmpData).getImei(),
                                    tmpPacket.getAddress()));
                        }

                        //如果是偶数，则转发
                        if ((tmpData[0] & 1) == 0) {
                            sendExceptOne(tmpData, tmpPacket.getAddress());
                        }
                    }
                }
            }
        }

        /**
         * 对外公开发送消息的接口，消息将被投送到发送队列中去
         *
         * @param data 数据
         */
        public void send(byte[] data) {
            if (!sendQueue.offer(data)) {
                LogUtil.e("消息[类型：" + data[0] + "]未发送！");
            }
        }

        /**
         * 对外公开接受消息的接口，数据报将被投送到接收队列中去
         *
         * @param packet 数据报
         */
        public void receive(DatagramPacket packet) {
            if (!receiveQueue.offer(packet)) {
                LogUtil.e("消息[类型：" + packet.getData()[0] + "]未接收！");
            }
        }

        private void sendExceptOne(byte[] data, InetAddress address) {
            for (ClientEntity entity : mClients) {
                if (entity.address.equals(address)) continue;
                try {
                    mSocket.send(new DatagramPacket(data, 0, data.length, entity.address, IConfig.LOCAL_PORT));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendToOne(byte[] data, InetAddress address) {
            try {
                mSocket.send(new DatagramPacket(data, 0, data.length, address, IConfig.LOCAL_PORT));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        class ClientEntity {
            InetAddress address;
            String imei;
            int noReplyCount;

            ClientEntity(String imei, InetAddress address) {
                this.imei = imei;
                this.address = address;
                this.noReplyCount = 0;
            }
        }
    }

    @Override
    public void send(byte[] data) {
        mDataHandle.send(data);
    }

    @Override
    public void close() {
        isRunning = false;
        if (mServerSocket != null)
            mServerSocket.close();
    }
}
