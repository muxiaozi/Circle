package cn.muxiaozi.circle.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import cn.muxiaozi.circle.core.IConfig;

public class TcpClient implements Runnable, ISocket {
    //IP
    private String mRemoteIP;

    //socket对象
    private Socket mSocket;

    //输入输出流
    private DataInputStream dis;
    private DataOutputStream dos;

    //是否连接
    private boolean isRunning;

    TcpClient(String remoteIP) {
        mRemoteIP = remoteIP;

        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void run() {

        try {
            mSocket = new Socket(mRemoteIP, IConfig.LOCAL_PORT);

            //获取输入输出流
            dos = new DataOutputStream(mSocket.getOutputStream());
            dis = new DataInputStream(mSocket.getInputStream());

            //把自己的信息发送到服务器


            //数据包长度
            int length = -1;
            while (isRunning && (length = dis.readInt()) != -1) {
                //获取数据包
                final byte[] data = new byte[length];
                dis.readFully(data, 0, length);

                //处理数据包
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (mSocket != null)
                    mSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void send(byte[] data) {
        if (dos != null) {
            try {
                dos.writeInt(data.length);
                dos.write(data, 0, data.length);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        isRunning = false;
        try {
            if (mSocket != null)
                mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
