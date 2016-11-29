package cn.muxiaozi.circle.libgdx.flappy_bird.actor;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import cn.muxiaozi.circle.libgdx.flappy_bird.MainGame;
import cn.muxiaozi.circle.libgdx.flappy_bird.Res;

/**
 * Created by 慕宵子 on 2016/8/17 0017.
 * Email: 1002042998@qq.com
 */
public class ReadyActor extends NumGroup {

    private int timeCount;

    private float deltaTime;

    private OnReadyListener mOnReadyListener;

    public interface OnReadyListener {
        void update(int time);
    }

    public ReadyActor(MainGame mainGame) {
        super(mainGame);

        Array<TextureAtlas.AtlasRegion> atlasRegions = mainGame.getAtlas().findRegions(Res.Atlas.IMAGE_NUM_BIG_00_TO_09);
        TextureRegion[] digitRegions = new TextureRegion[atlasRegions.size];
        for (int i = 0; i < atlasRegions.size; i++) {
            digitRegions[i] = atlasRegions.get(i);
        }
        setDigitRegions(digitRegions);

        setScale(2.5F);
        setOrigin(Align.center);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        deltaTime += delta;

        if(deltaTime < 4){
            if((int)deltaTime != timeCount){
                timeCount = (int) deltaTime;
                if(mOnReadyListener != null){
                    mOnReadyListener.update(3 - timeCount);
                }
            }
        }
    }

    public void startReady(OnReadyListener listener) {
        deltaTime = 0;
        timeCount = 0;
        this.mOnReadyListener = listener;
        this.mOnReadyListener.update(3);
    }
}
