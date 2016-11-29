package cn.muxiaozi.circle.libgdx.flappy_bird.actor.base;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cn.muxiaozi.circle.libgdx.flappy_bird.MainGame;
import cn.muxiaozi.circle.libgdx.BaseActor;

/**
 * 演员基类
 * 
 * @xietansheng
 */
public abstract class BaseImageActor extends BaseActor {

    private MainGame mainGame;

    public BaseImageActor(MainGame mainGame) {
        this.mainGame = mainGame;
    }

    public BaseImageActor(MainGame mainGame, TextureRegion region) {
        super(region);
        this.mainGame = mainGame;
    }

    public MainGame getMainGame() {
        return mainGame;
    }

    public void setMainGame(MainGame mainGame) {
        this.mainGame = mainGame;
    }

}















