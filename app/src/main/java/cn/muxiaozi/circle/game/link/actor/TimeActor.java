package cn.muxiaozi.circle.game.link.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by 慕宵子 on 2016/9/24 0024.
 * <p>
 * 时间
 */
public class TimeActor extends Actor {
    private TextureRegion foreground;

    private float value;
    private float startTime;
    private boolean isTimeOut;
    private OnTimeListener mListener;

    public interface OnTimeListener {
        void onTimeOut();
    }

    public TimeActor() {
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixmap.setColor(Color.GREEN);
        pixmap.fillRectangle(0, 0, 10, 10);
        foreground = new TextureRegion(new Texture(pixmap));
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Color tempBatchColor = batch.getColor();

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        batch.draw(foreground,
                getX() + 1, getY() + 1,
                getOriginX(), getOriginY(),
                getWidth() * value - 2, getHeight() - 2,
                getScaleX(), getScaleY(), getRotation());

        batch.setColor(tempBatchColor);
    }

    public void setOnTimeListener(OnTimeListener listener) {
        this.mListener = listener;
    }

    private void setValue(float value) {
        if (value < 0 || value > 1) {
            this.value = 0;
        } else {
            this.value = value;
        }
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
