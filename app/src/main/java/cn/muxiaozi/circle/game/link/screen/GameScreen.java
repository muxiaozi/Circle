package cn.muxiaozi.circle.game.link.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import cn.muxiaozi.circle.game.framwork.BaseScreen;
import cn.muxiaozi.circle.game.link.MainGame;
import cn.muxiaozi.circle.game.link.stage.GameStage;

/**
 * Created by 慕宵子 on 2016/9/21 0021.
 *
 * 游戏主场景
 */

public class GameScreen extends BaseScreen<MainGame> {

    //游戏舞台
    private GameStage gameStage;

    public GameScreen(MainGame game) {
        super(game);
        gameStage = new GameStage(game,
                new StretchViewport(game.getWorldWidth(), game.getWorldHeight()));
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
    public boolean receive(byte[] data) {
        return gameStage.receive(data);
    }
}
