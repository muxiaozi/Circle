package cn.muxiaozi.circle.game.flappy_bird.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import cn.muxiaozi.circle.game.flappy_bird.MainGame;
import cn.muxiaozi.circle.game.flappy_bird.stage.GameOverStage;
import cn.muxiaozi.circle.game.flappy_bird.stage.GameStage;
import cn.muxiaozi.circle.game.framwork.BaseScreen;

/**
 * 主游戏场景
 *
 * @xietansheng
 */
public class GameScreen extends BaseScreen<MainGame> {

    /**
     * 主游戏舞台
     */
    private GameStage gameStage;

    /**
     * 游戏结束舞台
     */
    private GameOverStage gameOverStage;

    public GameScreen(MainGame mainGame, String[] players) {
        super(mainGame);

        // 创建主游戏舞台
        gameStage = new GameStage(
                getGame(),
                new StretchViewport(
                        getGame().getWorldWidth(),
                        getGame().getWorldHeight()),
                players
        );

        // 创建游戏结束舞台
        gameOverStage = new GameOverStage(
                getGame(),
                new StretchViewport(
                        getGame().getWorldWidth(),
                        getGame().getWorldHeight()
                )
        );
        gameOverStage.setVisible(false);

        // 将输入处理设置到主游戏舞台
        Gdx.input.setInputProcessor(gameStage);
    }

    /**
     * 显示游戏结束舞台
     */
    public void showGameOverStage(int currScore) {
        // 游戏结束舞台可见
        gameOverStage.setVisible(true);

        // 将输入处理设置到游戏结束舞台
        Gdx.input.setInputProcessor(gameOverStage);

        // 设置当前分数
        gameOverStage.setCurrentScore(currScore);
    }

    /**
     * 重新开始准备游戏
     */
    public void restartReadyGame() {
        // 游戏结束舞台不可见
        gameOverStage.setVisible(false);

        // 将输入处理设置回主游戏舞台
        Gdx.input.setInputProcessor(gameStage);

        // 更新游戏准备状态
        gameStage.ready();
    }

    @Override
    public void render(float delta) {
        // 更新并绘制舞台（主游戏舞台优先处理）
        if (gameStage.isVisible()) {
            gameStage.act();
            gameStage.draw();
        }

        if (gameOverStage.isVisible()) {
            gameOverStage.act();
            gameOverStage.draw();
        }
    }

    @Override
    public void dispose() {
        // 场景销毁时, 同时销毁所有的舞台
        if (gameStage != null) {
            gameStage.dispose();
        }
        if (gameOverStage != null) {
            gameOverStage.dispose();
        }
    }

    @Override
    public boolean receive(byte[] data) {
        boolean flag;
        if (gameOverStage.isVisible()) {
            flag = gameOverStage.receive(data);
        } else {
            flag = gameStage.receive(data);
        }
        return flag;
    }
}


























