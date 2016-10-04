package cn.muxiaozi.circle.game.link.actor;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cn.muxiaozi.circle.game.framwork.BaseActor;
import cn.muxiaozi.circle.game.link.Res;

/**
 * Created by 慕宵子 on 2016/9/26 0026.
 */

public class ReadyActor extends BaseActor {
    private float startTime;
    private int value;

    public interface OnReadyListener {
        void onReady(int time);
    }

    private OnReadyListener mListener;

    private TextureAtlas atlas;

    public ReadyActor(TextureAtlas atlas) {
        this.atlas = atlas;
        value = 0;
    }

    public void setOnReadyListener(OnReadyListener listener) {
        this.mListener = listener;
    }

    @Override
    public void setRegion(TextureRegion region) {
        this.region = region;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        startTime -= delta;

        if (value != (int) startTime + 1) {
            value = (int) (startTime) + 1;
            setRegion(value != 0 ? atlas.findRegion(Res.Atlas.GRADE, value) : null);
            if (mListener != null) {
                mListener.onReady(value);
            }
        }
    }

    public void setValue(int value) {
        //设置startTime为0是为了不让其干扰value的状态
        startTime = 0;
        if (value > 0 || value <= 9) {
            setRegion(atlas.findRegion(Res.Atlas.GRADE, value + 1));
        } else {
            setRegion(null);
        }
    }

    public void start() {
        value = 3;
        startTime = 3.0F;
    }
}
