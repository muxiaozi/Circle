package cn.muxiaozi.circle.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.room.UserBean;
import cn.muxiaozi.circle.utils.LogUtil;

class TcpServer implements Runnable, ISocket {

    //服务端ServerSocket对象
    private static ServerSocket mServerSocket;

    private final Set<Socket> mSockets = Collections.synchronizedSet(
            new HashSet<Socket>(Constants.MAX_CLIENT_NUM));

    //保存Service对象
    private IDataService mDelivery;

    //Server运行状态
    private boolean isRunning;

    TcpServer(IDataService delivery) {
        mDelivery = delivery;

        //开启端口监听线程
        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket(Constants.LOCAL_PORT);
            LogUtil.i("服务器监听端口" + Constants.LOCAL_PORT + "...");

            while (isRunning) {
                Socket socket = mServerSocket.accept();

                //检测用户是否已满
                if (mSockets.size() < Constants.MAX_CLIENT_NUM) {
                    new Client(socket).start();
                } else {
                    socket.close();
                }
            }

        } catch (Exception ignored) {
        } finally {
            LogUtil.i("Server已关闭!");
            mDelivery.receive(DataFactory.packDisconnectServer());
            try {
                if (mServerSocket != null) {
                    mServerSocket.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 消息监听类
     *
     * @author 慕宵子
     */
    private class Client extends Thread {
        private Socket socket;
        private DataInputStream dis;
        private DataOutputStream dos;
        private String imei;

        Client(Socket socket) {
            this.socket = socket;
            mSockets.add(this.socket);
        }

        @Override
        public void run() {
            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());

                //给新连入的客户端发送大厅中的其他人信息
                mDelivery.getOnlineUserInfo(new DataService.IGetUserInfo() {
                    @Override
                    public void onGet(UserBean bean) {
                        sendDataToSender(DataFactory.packFriendIn(bean));
                    }
                });

                int length; //获取数据包长度
                while (isRunning && (length = dis.readInt()) != -1) {
                    //读取数据包
                    byte[] data = new byte[length];
                    dis.readFully(data, 0, length);

                    //如果是新加入的信息，则记录下来
                    if (data[0] == DataFactory.TYPE_FRIEND_IN) {
                        mDelivery.getOnlineUserInfo(new DataService.IGetUserInfo() {
                            @Override
                            public void onGet(UserBean bean) {
                                sendDataToSender(DataFactory.packFriendIn(bean));
                            }
                        });
                        imei = DataFactory.unpackFriendIn(data).getImei();
                        LogUtil.i("新用户：" + imei);
                    }

                    mDelivery.receive(data);

                    //如果是偶数就转发
                    if ((data[0] & 1) == 0) {
                        sendDataExceptOne(data, socket);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                LogUtil.i("客户端" + imei + "断开连接...");

                //给所有客户端发送某个客户离开的消息
                if (imei != null) {
                    final byte[] outData = DataFactory.packFriendOut(imei);
                    sendDataExceptOne(outData, socket);
                    mDelivery.receive(outData);
                }

                //把客户端从客户端集合中移除
                mSockets.remove(socket);

                try {
                    if (socket != null)
                        socket.close();
                } catch (Exception ignored) {
                }
            }
        }

        //发送消息至发送方
        private void sendDataToSender(byte[] data) {
            try {
                dos.writeInt(data.length);
                dos.write(data, 0, data.length);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //发送消息至除[except_socket]外所有客户端
    private void sendDataExceptOne(byte[] bytes, Socket except_socket) {
        for (Socket socket : mSockets) {
            if (socket.equals(except_socket)) continue;
            try {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(bytes.length);
                dos.write(bytes, 0, bytes.length);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void send(byte[] data) {
        sendDataExceptOne(data, null);
    }

    //关闭Server
    @Override
    public void close() {
        isRunning = false;
        try {
            //关闭Server，取消监听端口
            if (mServerSocket != null)
                mServerSocket.close();

            //逐个关闭客户端Socket
            if (mSockets != null) {
                for (Socket socket : mSockets) {
                    if (socket != null) socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
