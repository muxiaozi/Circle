package cn.muxiaozi.circle.game.framwork;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import cn.muxiaozi.circle.net.IReceiver;
import cn.muxiaozi.circle.net.ISender;

/**
 * Created by 慕宵子 on 2016/7/31 0031.
 */
public abstract class BaseGame extends Game implements IReceiver {

    private ISender mSender;
    private String[] mPlayers;

    public BaseGame(String[] players) {
        this.mPlayers = players;
    }

    public String[] getPlayers() {
        return mPlayers;
    }

    /**
     * 设置消息发送者，使用之来发送游戏数据
     *
     * @param sender 消息发送者
     */
    public void setSender(ISender sender) {
        this.mSender = sender;
    }

    /**
     * 接收数据
     *
     * @param data 数据
     */
    @Override
    public void receive(byte[] data) {
        final Screen screen = getScreen();
        if(screen != null){
            ((BaseScreen)screen).receive(data);
        }
    }

    /**
     * 发送数据
     *
     * @param data 数据
     */
    public void send(byte[] data) {
        if (mSender != null) {
            mSender.send(data);
        }
    }
}
