package cn.muxiaozi.circle.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.room.UserBean;
import cn.muxiaozi.circle.utils.LogUtil;

/**
 * Created by 慕宵子 on 2016/7/17.
 * <p>
 * UDP客户端
 */
class UDPClient implements Runnable, ISocket {

    private DatagramSocket mSocket;
    private DatagramPacket mReceiverPacket;

    private IDataService mListener;

    private DataHandle mDataHandle;

    private boolean isRunning;

    private SocketAddress mServerAddress;

    public UDPClient(IDataService listener, String remoteIP) {
        mListener = listener;

        mReceiverPacket = new DatagramPacket(new byte[512], 512);
        mServerAddress = new InetSocketAddress(remoteIP, Constants.LOCAL_PORT);

        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            mSocket = new DatagramSocket(Constants.LOCAL_PORT);

            mSocket.connect(mServerAddress);

            mDataHandle = new DataHandle(mSocket);
            mDataHandle.setPriority(Thread.MAX_PRIORITY);
            mDataHandle.start();

            mSocket.setSoTimeout(3000);

            //发送自己的信息给服务器
            mListener.getMyInfo(new DataService.IGetUserInfo() {
                @Override
                public void onGet(UserBean bean) {
                    send(DataFactory.packFriendIn(bean));
                }
            });

            while (isRunning) {
                mSocket.receive(mReceiverPacket);
                mDataHandle.receive(mReceiverPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mListener.receive(DataFactory.packDisconnectServer());
            close();
        }
    }

    private final class DataHandle extends Thread {
        //发送数据队列
        private BlockingQueue<byte[]> sendQueue;

        //接收数据队列
        private BlockingQueue<DatagramPacket> receiveQueue;

        //临时数据包
        private byte[] tmpData;
        private DatagramPacket tmpPacket;

        public DataHandle(DatagramSocket socket) {
            mSocket = socket;

            sendQueue = new ArrayBlockingQueue<>(5);
            receiveQueue = new ArrayBlockingQueue<>(5);
        }

        long time;

        @Override
        public void run() {
            while (isRunning) {

                //处理发送队列
                if ((tmpData = sendQueue.poll()) != null && mSocket != null) {
                    sendToServer(tmpData);
                }

                //处理接受队列
                if ((tmpPacket = receiveQueue.poll()) != null) {
                    tmpData = tmpPacket.getData();
                    if(tmpData[0] == 5){
                        LogUtil.i("产生管道...");
                    }

                    if (tmpData[0] == DataFactory.TYPE_HEART_BEAT) {
                        sendToServer(DataFactory.packHeartBeat());
                    } else {
                        mListener.receive(tmpData);
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

        private void sendToServer(byte[] data) {
            try {
                mSocket.send(new DatagramPacket(data, 0, data.length));
            } catch (IOException e) {
                e.printStackTrace();
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
        if (mSocket != null)
            mSocket.close();
    }
}
