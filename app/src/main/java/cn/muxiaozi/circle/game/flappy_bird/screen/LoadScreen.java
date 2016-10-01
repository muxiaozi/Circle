package cn.muxiaozi.circle.game.flappy_bird.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.game.flappy_bird.MainGame;
import cn.muxiaozi.circle.game.flappy_bird.DataFactory;
import cn.muxiaozi.circle.game.framwork.BaseScreen;
import cn.muxiaozi.circle.net.DataService;
import cn.muxiaozi.circle.utils.InfoUtil;

/**
 * Created by 慕宵子 on 2016/8/13 0013.
 * Email: 1002042998@qq.com
 * <p/>
 * 手机一般分辨率：360 * 640
 * <p/>
 * 这个类的作用就是等待所有玩家报告就绪即跳入下一个画面
 */
public class LoadScreen extends BaseScreen<MainGame> {
    //记录玩家是否准备完毕
    private Array<PlayerState> playerStates;

    //玩家的imei列表
    private String[] players;

    //自己的IMEI号码
    private String myImei;

    //等待计时
    private float waitTime = 0;

    //发送准备要求的时间间隔（0.5s）
    private static final float REQUEST_INTERVAL = 0.5F;

    private static class PlayerState {
        String imei;
        boolean isPrepared;

        PlayerState(String imei) {
            this.imei = imei;
            this.isPrepared = false;
        }
    }

    public LoadScreen(MainGame game) {
        super(game);
        this.myImei = Gdx.app.getPreferences(Constants.CIRCLE_CONFIG)
                .getString(InfoUtil.IMEI);
    }

    @Override
    public void render(float delta) {
        waitTime += delta;

        if (DataService.isServer()) {
            if (isAllPrepared()) {
                getGame().send(DataFactory.packStartGame(this.players));
                getGame().startGame(this.players);
            } else if (waitTime > REQUEST_INTERVAL) {
                getGame().send(DataFactory.packRequestPrepare());
                waitTime = 0;
            }
        }
    }

    @Override
    public void dispose() {
        playerStates.clear();
    }

    @Override
    public void receive(byte[] data) {
        switch (data[0]) {
            case DataFactory.TYPE_REQUEST_PREPARE:
                getGame().send(DataFactory.packPrepare(myImei));
                break;

            case DataFactory.TYPE_START_GAME:
                players = DataFactory.unpackStartGame(data);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        getGame().startGame(players);
                    }
                });
                break;

            case DataFactory.TYPE_PREPARE:
                String imei = DataFactory.unpackPrepare(data);
                prepare(imei);
                break;
        }
    }

    /**
     * 玩家准备
     *
     * @param imei
     */
    private void prepare(String imei) {
        if (playerStates != null) {
            for (PlayerState playerState : playerStates) {
                if (playerState.imei.equals(imei)) {
                    playerState.isPrepared = true;
                    break;
                }
            }
        }
    }

    /**
     * 检测所有玩家是否都准备就绪
     *
     * @return
     */
    private boolean isAllPrepared() {
        if (playerStates == null) return false;

        for (PlayerState state : playerStates) {
            if (!state.isPrepared)
                return false;
        }
        return true;
    }

    /**
     * 只有当前为服务器时，此方法才会被调用
     *
     * @param players
     */
    public void setPlayerList(String[] players) {
        this.players = players;

        //初始化准备状态表
        playerStates = new Array<>(this.players.length);
        for (String imei : players) {
            playerStates.add(new PlayerState(imei));
        }

        //更新自己的准备状态
        prepare(myImei);

        //要求准备
        getGame().send(DataFactory.packRequestPrepare());
    }
}
