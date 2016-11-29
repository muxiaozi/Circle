package cn.muxiaozi.circle.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import cn.muxiaozi.circle.base.IConfig;
import cn.muxiaozi.circle.room.UserBean;

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

    UDPClient(IDataService listener, String remoteIP) {
        mListener = listener;

        mReceiverPacket = new DatagramPacket(new byte[512], 512);
        mServerAddress = new InetSocketAddress(remoteIP, IConfig.LOCAL_PORT);

        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            mSocket = new DatagramSocket(IConfig.LOCAL_PORT);

            mSocket.connect(mServerAddress);

            mDataHandle = new DataHandle(mSocket);
            mDataHandle.setPriority(Thread.MAX_PRIORITY);
            mDataHandle.start();

            mSocket.setSoTimeout(3000);

            //发送自己的信息给服务器
            mListener.getMyInfo(new IDataService.IGetUserInfo() {
                @Override
                public void onGet(UserBean bean) {
                    send(DataFactory.packFriendIn(bean));
                }
            });

            while (isRunning) {
                mSocket.receive(mReceiverPacket);
                mListener.receive(mReceiverPacket.getData());
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

        //临时数据包
        private byte[] tmpData;

        DataHandle(DatagramSocket socket) {
            mSocket = socket;
            sendQueue = new ArrayBlockingQueue<>(5);
        }

        @Override
        public void run() {
            while (isRunning) {
                //处理发送队列
                if ((tmpData = sendQueue.poll()) != null && mSocket != null) {
                    try {
                        mSocket.send(new DatagramPacket(tmpData, 0, tmpData.length));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * 对外公开发送消息的接口，消息将被投送到发送队列中去
         *
         * @param data 数据
         */
        public void send(byte[] data) throws InterruptedException {
            sendQueue.put(data);
        }
    }

    @Override
    public void send(byte[] data) {
        try {
            mDataHandle.send(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        isRunning = false;
        if (mSocket != null)
            mSocket.close();
    }
}
