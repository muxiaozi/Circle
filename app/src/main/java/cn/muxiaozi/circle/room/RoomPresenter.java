package cn.muxiaozi.circle.room;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.ArrayList;

import cn.muxiaozi.circle.core.BasePresenter;
import cn.muxiaozi.circle.core.SystemPacket;
import cn.muxiaozi.circle.core.CoreService;
import cn.muxiaozi.circle.net.IReceiver;
import cn.muxiaozi.circle.utils.AsyncRun;
import cn.muxiaozi.circle.utils.Config;

/**
 * Created by 慕宵子 on 2016/7/24.
 */
class RoomPresenter extends BasePresenter<RoomView> implements IReceiver {
    private String myImei;
    private CoreService.MessageBinder mDeliver;

    RoomPresenter(Context context, RoomView view) {
        super(context, view);

        //绑定消息服务器
        Intent intent = new Intent(context, CoreService.class);
        context.bindService(intent, mConn, Context.BIND_AUTO_CREATE);

        myImei = Config.getImei(context);
    }

    /**
     * 绑定消息Service
     */
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDeliver = (CoreService.MessageBinder) service;
            mDeliver.addObserver(RoomPresenter.this);

            //根据在线玩家列表初始化房间
            ArrayList<UserBean> playList = mView.getPlayerList();
            mDeliver.fillPlayerList(playList);

            //如果是服务器，默认是准备状态
            if (CoreService.isServer()) {
                prepare();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mDeliver != null) {
                mDeliver.removeObserver(RoomPresenter.this);
            }
        }
    };

    @Override
    public void receive(byte[] data) {
        switch (data[0]) {
            case SystemPacket.TYPE_PREPARE:
                SystemPacket.PrepareEntity playerEntity = SystemPacket.unpackPrepareState(data);
                ArrayList<UserBean> players = mView.getPlayerList();
                for (UserBean player : players) {
                    if (player.getImei().equals(playerEntity.imei)) {
                        player.setPrepare(playerEntity.isPrepare);
                        break;
                    }
                }
                updatePlayerList();
                break;

            case SystemPacket.TYPE_START_GAME:
                final SystemPacket.StartGameEntity entity = SystemPacket.unpackStartGame(data);
                AsyncRun.run(new Runnable() {
                    @Override
                    public void run() {
                        mView.startGame(entity);
                    }
                });
                break;

            case SystemPacket.TYPE_FRIEND_IN:
                mDeliver.fillPlayerList(mView.getPlayerList());
                updatePlayerList();
                break;

            case SystemPacket.TYPE_FRIEND_OUT:
                mDeliver.fillPlayerList(mView.getPlayerList());
                updatePlayerList();
                break;

            case SystemPacket.TYPE_DISCONNECT_SERVER:
                AsyncRun.run(new Runnable() {
                    @Override
                    public void run() {
                        mView.exit();
                    }
                });
                break;
        }
    }

    /**
     * 检测所有玩家是否都准备完毕
     *
     * @return 准备结果
     */
    @Override
    public boolean isAllPlayerPrepared() {
        ArrayList<UserBean> players = mView.getPlayerList();
        for (UserBean player : players) {
            if (!player.isPrepare())
                return false;
        }
        return true;
    }

    @Override
    public void prepare() {
        mDeliver.setPrepare(true);

        mDeliver.send(SystemPacket.packPrepareState(
                new SystemPacket.PrepareEntity(myImei, true)));

        ArrayList<UserBean> players = mView.getPlayerList();
        for (UserBean player : players) {
            if (player.getImei().equals(myImei)) {
                player.setPrepare(true);
                break;
            }
        }
        updatePlayerList();
    }

    @Override
    public void cancelPrepare() {
        mDeliver.setPrepare(false);

        mDeliver.send(SystemPacket.packPrepareState(
                new SystemPacket.PrepareEntity(myImei, false)));

        ArrayList<UserBean> players = mView.getPlayerList();
        for (UserBean player : players) {
            if (player.getImei().equals(myImei)) {
                player.setPrepare(false);
                break;
            }
        }
        updatePlayerList();
    }

    @Override
    public void startGame(SystemPacket.StartGameEntity entity) {
        mDeliver.send(SystemPacket.packStartGame(entity));
    }

    private void updatePlayerList() {
        AsyncRun.run(new Runnable() {
            @Override
            public void run() {
                mView.updatePlayerList();
            }
        });
    }

    @Override
    public void onDestroy() {
        //退出房间时，自动取消准备
        if (mDeliver != null) {
            mDeliver.removeObserver(RoomPresenter.this);
        }

        mContext.unbindService(mConn);
    }
}
