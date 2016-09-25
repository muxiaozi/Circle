package cn.muxiaozi.circle.game.flappy_bird;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.badlogic.gdx.backends.android.AndroidApplication;

import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.game.framwork.BaseGame;
import cn.muxiaozi.circle.net.DataService;

/**
 * Created by 慕宵子 on 2016/8/3 0003.
 */
public class FlappyBirdActivity extends AndroidApplication {
    private DataService.MessageBinder mDeliver;
    private MainGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取房间中的玩家列表
        final String[] players = getIntent().getStringArrayExtra(Constants.KEY_USER_INFO);
        game = new MainGame(players);

        initialize(game);
        bindService(new Intent(this, DataService.class), conn, BIND_AUTO_CREATE);
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDeliver = (DataService.MessageBinder) service;
            mDeliver.setGameDataListener(game);
            game.setSender(new BaseGame.ISender() {
                @Override
                public void send(byte[] data) {
                    mDeliver.send(data);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mDeliver != null) {
                mDeliver.setGameDataListener(null);
            }
        }
    };

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
}
