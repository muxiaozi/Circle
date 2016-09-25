package cn.muxiaozi.circle.net;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.room.UserBean;
import cn.muxiaozi.circle.utils.AsyncRun;
import cn.muxiaozi.circle.utils.InfoUtil;
import cn.muxiaozi.circle.utils.LogUtil;
import cn.muxiaozi.circle.utils.ToastUtil;

public class DataService extends Service implements IDataService {

    // 终端的三种状态: 无连接、服务器、客户端
    public static final int STATE_NONE = 0;
    public static final int STATE_SERVER = 1;
    public static final int STATE_CLIENT = 2;

    private static int mState = STATE_NONE;

    //UDP服务端或客户端实例
    private ISocket mSocket;

    //维护一个在线朋友列表
    private final HashMap<String, UserBean> mOnlineFriends =
            new HashMap<>(Constants.MAX_CLIENT_NUM);

    //消息监听接口
    private IReceiver mGameDataListener;
    private IReceiver mRoomDataListener;

    @Override
    public IBinder onBind(Intent intent) {
        return new MessageBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mOnlineFriends.clear();
        mOnlineFriends.put(InfoUtil.getImei(this), InfoUtil.getMyInfo(this));
    }

    /**
     * 获取当前角色状态
     *
     * @return 服务端、客户端或者None中的一种
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
                    mOnlineFriends.clear();
                    mOnlineFriends.put(InfoUtil.getImei(this), InfoUtil.getMyInfo(this));
                    if (mSocket != null) {
                        mSocket.close();
                    }
                    break;

                case STATE_SERVER:
                    mSocket = new TcpServer(this);
                    break;

                case STATE_CLIENT:
                    final String remoteIP = intent.getExtras().getString(Constants.KEY_REMOTE_IP);
                    mSocket = new TcpClient(this, remoteIP);
                    break;
            }
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void getMyInfo(IGetUserInfo receiver) {
        receiver.onGet(InfoUtil.getMyInfo(this));
    }

    @Override
    public void getOnlineUserInfo(IGetUserInfo receiver) {
        Collection<UserBean> users = mOnlineFriends.values();
        for (UserBean user : users) {
            receiver.onGet(user);
        }
    }

    public void onFriendIn(UserBean bean) {
        if (bean != null) {
            mOnlineFriends.put(bean.getImei(), bean);
        }
    }

    public void onFriendOut(String imei) {
        mOnlineFriends.remove(imei);
    }

    @Override
    public boolean receive(byte[] data) {
        if (data[0] < 0) {
            switch (data[0]) {
                case DataFactory.TYPE_FRIEND_IN:    //朋友加入数据
                    onFriendIn(DataFactory.unpackFriendIn(data));
                    break;
                case DataFactory.TYPE_FRIEND_OUT:   //朋友退出数据
                    onFriendOut(DataFactory.unpackFriendOut(data));
                    break;
                case DataFactory.TYPE_PREPARE:      //玩家在游戏大厅改变准备状态
                    DataFactory.PrepareEntity entity = DataFactory.unpackPrepareState(data);
                    UserBean player = mOnlineFriends.get(entity.imei);
                    if (player != null) {
                        player.setPrepare(entity.isPrepare);
                    }
                    break;
                case DataFactory.TYPE_DISCONNECT_SERVER:
                    onDisconnectToServer();
                    break;
            }

            if (mRoomDataListener != null) {
                mRoomDataListener.receive(data);
            }

            if (mGameDataListener != null) {
                mGameDataListener.receive(data);
            }

            return false;
        }

        return mGameDataListener == null || mGameDataListener.receive(data);
    }

    public void onDisconnectToServer() {
        mState = STATE_NONE;

        mOnlineFriends.clear();
        mOnlineFriends.put(InfoUtil.getImei(this), InfoUtil.getMyInfo(this));

        Intent intent = new Intent(Constants.ACTION_DISCONNECT);
        sendBroadcast(intent);
    }

    /**
     * 自定义返回本地Binder对象
     */
    public class MessageBinder extends Binder {

        //发送消息
        public void send(byte[] data) {
            if (mState == STATE_NONE) {
                LogUtil.w("请先加入圈圈！");
                AsyncRun.run(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(DataService.this, "请先加入圈圈！");
                    }
                });
            } else {
                mSocket.send(data);
            }
        }

        //获取在线朋友列表
        public void fillPlayerList(ArrayList<UserBean> list) {
            list.clear();
            Collection<UserBean> users = mOnlineFriends.values();
            for (UserBean user : users) {
                list.add(user);
            }
        }

        // 设置自己的准备状态
        public void setPrepare(boolean isPrepare) {
            mOnlineFriends.get(InfoUtil.getImei(DataService.this)).setPrepare(isPrepare);
        }

        // 设置游戏消息监听器
        public void setGameDataListener(IReceiver l) {
            mGameDataListener = l;
        }

        // 设置房间消息监听器
        public void setRoomDataListener(IReceiver l) {
            mRoomDataListener = l;
        }
    }

    @Override
    public void onDestroy() {
        if (mState != STATE_NONE && mSocket != null) {
            mSocket.close();
        }
        super.onDestroy();
    }
}