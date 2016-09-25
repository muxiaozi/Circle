package cn.muxiaozi.circle.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.room.UserBean;
import cn.muxiaozi.circle.utils.LogUtil;

public class TcpClient implements Runnable, ISocket {
    //IP
    private String mRemoteIP;

    //socket对象
    private Socket mSocket;

    //处理消息的Service
    private IDataService mDelivery;

    //输入输出流
    private DataInputStream dis;
    private DataOutputStream dos;

    //是否连接
    private boolean isRunning;

    public TcpClient(IDataService listener, String remoteIP) {
        mRemoteIP = remoteIP;
        mDelivery = listener;

        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void run() {

        try {
            mSocket = new Socket(mRemoteIP, Constants.LOCAL_PORT);
            LogUtil.i("Client成功连接到服务器...");

            //获取输入输出流
            dos = new DataOutputStream(mSocket.getOutputStream());
            dis = new DataInputStream(mSocket.getInputStream());

            //把自己的信息发送到服务器
            mDelivery.getMyInfo(new DataService.IGetUserInfo() {
                @Override
                public void onGet(UserBean bean) {
                    send(DataFactory.packFriendIn(bean));
                }
            });

            //数据包长度
            int length = -1;
            while (isRunning && (length = dis.readInt()) != -1) {
                //获取数据包
                final byte[] data = new byte[length];
                dis.readFully(data, 0, length);

                //处理数据包
                mDelivery.receive(data);
            }
            LogUtil.i("Client断开:isRunning:" + isRunning + ",length = " + length);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("Client断开:" + e.toString());
        } finally {
            mDelivery.receive(DataFactory.packDisconnectServer());
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
