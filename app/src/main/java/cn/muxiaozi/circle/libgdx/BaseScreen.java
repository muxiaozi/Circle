package cn.muxiaozi.circle.libgdx;

import com.badlogic.gdx.Screen;

import cn.muxiaozi.circle.net.IReceiver;

/**
 * Created by 慕宵子 on 2016/8/2 0002.
 */
public abstract class BaseScreen<T extends BaseGame> implements Screen, IReceiver {

    private T game;

    public BaseScreen(T game) {
        this.game = game;
    }

    public T getGame() {
        return game;
    }

    public void setGame(T game) {
        this.game = game;
    }

    @Override
    public void show() {
    }

    @Override
    public abstract void render(float delta);

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public abstract void dispose();
}
