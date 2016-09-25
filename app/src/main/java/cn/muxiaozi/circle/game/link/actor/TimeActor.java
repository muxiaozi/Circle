package cn.muxiaozi.circle.game.link.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by 慕宵子 on 2016/9/24 0024.
 */

public class TimeActor extends ProgressBar {
    private float startTime;
    private boolean isTimeOut;
    private OnTimeListener mListener;

    public interface OnTimeListener {
        void onTimeOut();
    }

    public TimeActor(TextureRegion region1,TextureRegion region) {
        super(0f, 1f, 0.1f, true, new ProgressBarStyle(new TextureRegionDrawable(region1),
                new TextureRegionDrawable(region)));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        startTime += delta;

        if (startTime <= 6) {
            setValue((6 - startTime) / 6);
        } else {
            if (!isTimeOut) {
                isTimeOut = true;
                if (mListener != null) {
                    mListener.onTimeOut();
                }
            }
        }
    }

    public void setOnTimeListener(OnTimeListener listener) {
        this.mListener = listener;
    }

    /**
     * 开始
     */
    public void start() {
        startTime = 0;
        isTimeOut = false;
    }

    /**
     * 停止，通过此函数停止计时器不会触发TimeListener
     */
    public void stop() {
        startTime = 6F;
        isTimeOut = true;
    }
}
