package cn.muxiaozi.circle.libgdx.link.actor;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import cn.muxiaozi.circle.libgdx.BaseActor;
import cn.muxiaozi.circle.libgdx.link.Res;

/**
 * Created by 慕宵子 on 2016/9/26 0026.
 */

public class ReadyActor extends BaseActor {
    private float startTime;
    private int value;

    public interface OnReadyListener {
        void update(int time);
    }

    private OnReadyListener mListener;

    private TextureAtlas atlas;

    public ReadyActor(TextureAtlas atlas) {
        this.atlas = atlas;
        value = 3;
        startTime = 0;  //作用是为了一开始固定倒计时
        setValue(3);
    }

    public void setOnReadyListener(OnReadyListener listener) {
        this.mListener = listener;
        listener.update(3);
    }

    @Override
    public void setRegion(TextureRegion region) {
        this.region = region;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (startTime > 0) {
            startTime -= delta;
            if ((int) (startTime) != value) {//此处有更新
                setValue((int) startTime);
                if (mListener != null) {
                    mListener.update(value);
                }
            }
        }
    }

    /**
     * 设置倒计时显示数字
     *
     * @param value 数字
     */
    public void setValue(int value) {
        //设置startTime为0是为了不让其干扰value的状态
        if (value > 0 && value <= 9) {
            setRegion(atlas.findRegion(Res.Atlas.GRADE, value));
            this.value = value;
        } else {
            setRegion(null);
            this.value = 0;
        }
    }

    public void start() {
        value = 3;
        startTime = 3F;
    }
}
