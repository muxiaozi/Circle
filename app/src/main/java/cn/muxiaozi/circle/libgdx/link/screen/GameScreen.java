package cn.muxiaozi.circle.libgdx.link.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import cn.muxiaozi.circle.base.IConfig;
import cn.muxiaozi.circle.libgdx.BaseScreen;
import cn.muxiaozi.circle.libgdx.link.MainGame;
import cn.muxiaozi.circle.libgdx.link.stage.GameStage;
import cn.muxiaozi.circle.utils.Config;

/**
 * Created by 慕宵子 on 2016/9/21 0021.
 * <p>
 * 游戏主场景
 */

public class GameScreen extends BaseScreen<MainGame> {

    //游戏舞台
    private GameStage gameStage;

    public GameScreen(MainGame game) {
        super(game);
        int index = 0;
        String imei = Gdx.app.getPreferences(IConfig.CIRCLE_CONFIG).getString(Config.IMEI);
        for (int i = 0; i < game.getPlayers().length; i++) {
            if (game.getPlayers()[i].equals(imei)) {
                index = i;
                break;
            }
        }
        gameStage = new GameStage(game,
                new StretchViewport(game.getWorldWidth(), game.getWorldHeight()), index);
        Gdx.input.setInputProcessor(gameStage);
    }

    @Override
    public void render(float delta) {
        gameStage.act(delta);
        gameStage.draw();
    }

    @Override
    public void dispose() {
        gameStage.dispose();
    }

    @Override
    public void receive(byte[] data) {
        gameStage.receive(data);
    }
}
