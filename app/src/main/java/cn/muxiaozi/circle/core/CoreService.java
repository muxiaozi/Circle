package cn.muxiaozi.circle.core;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import cn.muxiaozi.circle.net.IReceiver;
import cn.muxiaozi.circle.net.ISender;
import cn.muxiaozi.circle.net.ISocket;
import cn.muxiaozi.circle.net.TcpClient;
import cn.muxiaozi.circle.net.TcpServer;
import cn.muxiaozi.circle.room.UserBean;
import cn.muxiaozi.circle.utils.AsyncRun;
import cn.muxiaozi.circle.utils.Config;
import cn.muxiaozi.circle.utils.ToastUtil;

public class CoreService extends Service implements IReceiver, Runnable {
    private static final String TAG = "CoreService";

    // 终端的三种状态: 无连接、服务器、客户端
    private static final int STATE_NONE = 0;
    private static final int STATE_SERVER = 1;
    private static final int STATE_CLIENT = 2;

    private static int mState = STATE_NONE;

    //服务端或客户端
    private ISocket mSocket;

    //维护一个在线朋友列表
    private HashMap<String, UserBean> mOnlineUsers;

    //消息队列
    private ArrayBlockingQueue<byte[]> mDataQueue;
    //消息轮询子线程
    private Thread mDataReceiveThread;
    //程序运行状态，默认运行
    private boolean isRunning;

    //游戏包处理
    private IReceiver mGPHandler;

    private MessageBinder mMessageBinder = new MessageBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mMessageBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //消息队列
        isRunning = true;
        mDataQueue = new ArrayBlockingQueue<>(32);
        mDataReceiveThread = new Thread(this);
        mDataReceiveThread.setPriority(Thread.MAX_PRIORITY);
        mDataReceiveThread.start();

        //在线列表
        mOnlineUsers = new HashMap<>(IConfig.MAX_CLIENT_NUM);
        mOnlineUsers.put(Config.getName(), Config.getMyInfo());
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                byte[] data = mDataQueue.take();
                byte type = AbsPacket.getType(data);
                if (type < 0) {//处理系统数据包
                    try {
                        AbsPacket packet = SystemPackets.getPacketOperator(type);
                        packet.toObj(data);
                        packet.handle(this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {//处理游戏数据包
                    if(mGPHandler != null) mGPHandler.receive(data, type);
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "run: 消息队列线程结束！");
            }
        }
    }

    @Override
    public void receive(byte[] data, byte type) {
        if (!mDataQueue.offer(data)) {
            Log.w(TAG, "receive: 消息池已满，丢失一条消息！");
        }

        //如果需要转发，那么转发
        if (AbsPacket.needTransmit(data)) {
            mSocket.send(data);
        }
    }

    /**
     * 是否为服务器
     *
     * @return 是否为服务器
     */
    public static boolean isServer() {
        return mState == STATE_SERVER;
    }

    /**
     * 每次调用startService都会调用此方法
     * 更新状态
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mState != intent.getFlags()) {
            mState = intent.getFlags();

            switch (mState) {
                case STATE_NONE:
                    mOnlineUsers.clear();
                    mOnlineUsers.put(Config.getName(), Config.getMyInfo());
                    if (mSocket != null) {
                        mSocket.close();
                    }
                    break;

                case STATE_SERVER:
                    mSocket = new UDPServer();
                    break;

                case STATE_CLIENT:
                    final String remoteIP = intent.getExtras().getString(IConfig.KEY_REMOTE_IP);
                    mSocket = new TcpClient(this, remoteIP);
                    break;
            }
        }

        return START_REDELIVER_INTENT;
    }

    public void getOnlineUserInfo(IGetUserInfo receiver) {
        Collection<UserBean> users = mOnlineUsers.values();
        for (UserBean user : users) {
            receiver.onGet(user);
        }
    }

    public void onFriendIn(UserBean bean) {
        if (bean != null) {
            mOnlineUsers.put(bean.getImei(), bean);
        }
    }

    public void onFriendOut(String imei) {
        mOnlineUsers.remove(imei);
    }

    public void onDisconnectToServer() {
        mState = STATE_NONE;

        mOnlineUsers.clear();
        mOnlineUsers.put(Config.getImei(this), Config.getMyInfo(this));

        Intent intent = new Intent(IConfig.ACTION_DISCONNECT);
        sendBroadcast(intent);
    }

    /**
     * 自定义返回本地Binder对象
     */
    public class MessageBinder extends Binder implements ISender{
        //发送消息
        @Override
        public void send(byte[] data) {
            if (mState != STATE_NONE) {
                mSocket.send(data);
            } else {
                Log.w(TAG, "send: 请先加入圈圈！");
                AsyncRun.run(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(CoreService.this, "请先加入圈圈！");
                    }
                });
            }
        }

        //获取在线朋友列表
        public void fillPlayerList(ArrayList<UserBean> list) {
            list.clear();
            Collection<UserBean> users = mOnlineUsers.values();
            for (UserBean user : users) {
                list.add(user);
            }
        }

        // 设置自己的准备状态
        public void setPrepare(boolean isPrepare) {
            mOnlineUsers.get(Config.getImei(CoreService.this)).setPrepare(isPrepare);
        }

        // 设置游戏数据监听者
        public void setGamePacketHandler(IReceiver receiver) {
            mGPHandler = receiver;
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        mDataReceiveThread.interrupt();
        if (mState != STATE_NONE && mSocket != null) {
            mSocket.close();
        }
        super.onDestroy();
    }
}
