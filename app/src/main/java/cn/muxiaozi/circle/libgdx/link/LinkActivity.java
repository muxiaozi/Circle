package cn.muxiaozi.circle.libgdx.link;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.badlogic.gdx.backends.android.AndroidApplication;

import cn.muxiaozi.circle.core.IConfig;
import cn.muxiaozi.circle.core.CoreService;
import cn.muxiaozi.circle.net.ISender;

/**
 * Created by 慕宵子 on 2016/9/18 0018.
 */
public class LinkActivity extends AndroidApplication {
    private CoreService.MessageBinder mDeliver;
    private MainGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取房间中的玩家列表
        final String[] players = getIntent().getStringArrayExtra(IConfig.KEY_USER_INFO);
        game = new MainGame(players);

        initialize(game);
        bindService(new Intent(this, CoreService.class), conn, BIND_AUTO_CREATE);
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDeliver = (CoreService.MessageBinder) service;
            mDeliver.addObserver(game);
            game.setSender(new ISender() {
                @Override
                public void send(byte[] data) {
                    mDeliver.send(data);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mDeliver != null) {
                mDeliver.removeObserver(game);
            }
        }
    };

    @Override
    protected void onDestroy() {
        if (mDeliver != null) {
            mDeliver.removeObserver(game);
        }
        unbindService(conn);
        super.onDestroy();
    }
}
