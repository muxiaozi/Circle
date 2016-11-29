package cn.muxiaozi.circle.libgdx;

import com.badlogic.gdx.scenes.scene2d.Group;

/**
 * Created by 慕宵子 on 2016/8/12 0012.
 * Email: 1002042998@qq.com
 */
public class BaseGroup<T extends BaseGame> extends Group {

    private T game;

    public BaseGroup(T game) {
        this.game = game;
    }

    public T getGame() {
        return game;
    }

    public void setGame(T game) {
        this.game = game;
    }

    /**
     * 设置上边界的坐标
     *
     * @param topY
     */
    public void setTopY(float topY) {
        setY(topY - getHeight());
    }

    /**
     * 获取上边界的坐标
     *
     * @return
     */
    public float getTopY() {
        return getY() + getHeight();
    }

    /**
     * 设置右边界的坐标
     *
     * @param rightX
     */
    public void setRightX(float rightX) {
        setX(rightX - getWidth());
    }

    /**
     * 获取右边的坐标
     *
     * @return
     */
    public float getRightX() {
        return getX() + getWidth();
    }

    /**
     * 设置中心点坐标
     *
     * @param centerX
     * @param centerY
     */
    public void setCenter(float centerX, float centerY) {
        setCenterX(centerX);
        setCenterY(centerY);
    }

    /**
     * 设置水平方向中心点坐标
     *
     * @param centerX
     */
    public void setCenterX(float centerX) {
        setX(centerX - getWidth() / 2.0F);
    }

    /**
     * 设置竖直方向中心点坐标
     *
     * @param centerY
     */
    public void setCenterY(float centerY) {
        setY(centerY - getHeight() / 2.0F);
    }
}
