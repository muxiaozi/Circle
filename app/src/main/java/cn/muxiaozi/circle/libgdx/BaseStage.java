package cn.muxiaozi.circle.libgdx;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

import cn.muxiaozi.circle.net.IReceiver;


/**
 * Created by 慕宵子 on 2016/8/2 0002.
 */
public abstract class BaseStage<T extends BaseGame> extends Stage implements IReceiver {
    private T game;
    private boolean visible = true;

    public BaseStage(T game) {
        super();
        this.game = game;
    }

    public BaseStage(T game, Viewport viewport) {
        super(viewport);
        this.game = game;
    }

    public T getGame() {
        return game;
    }

    public void setGame(T game) {
        this.game = game;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
