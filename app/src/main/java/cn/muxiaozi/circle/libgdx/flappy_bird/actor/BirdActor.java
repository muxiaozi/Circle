package cn.muxiaozi.circle.libgdx.flappy_bird.actor;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Align;

import cn.muxiaozi.circle.libgdx.flappy_bird.MainGame;
import cn.muxiaozi.circle.libgdx.flappy_bird.actor.base.BaseAnimationActor;
import cn.muxiaozi.circle.libgdx.flappy_bird.BirdState;
import cn.muxiaozi.circle.libgdx.flappy_bird.Res;


/**
 * 小鸟, 小鸟可以看做是在竖直方向上跳跃, 水平方向上不动
 *
 * @xietansheng
 */
public class BirdActor extends BaseAnimationActor {

    /**
     * 玩家信息
     */
    private String imei;

    /**
     * 当前游戏状态
     */
    private BirdState birdState;

    /**
     * 小鸟竖直方向上的速度
     */
    private float velocityY;

    /**
     * 分数
     */
    private int grade;

    /**
     * 小鸟演员
     *
     * @param mainGame 游戏实例
     */
    BirdActor(MainGame mainGame, String imei) {
        super(mainGame);

        this.imei = imei;

        // 创建小鸟动画
        Animation animation = new Animation(
                0.2F,
                getMainGame().getAtlas().findRegions(Res.Atlas.IMAGE_BIRD_YELLOW_01_TO_03)
        );
        // 动画循环播放
        animation.setPlayMode(Animation.PlayMode.LOOP);
        // 设置小鸟动画
        setAnimation(animation);

        setOrigin(Align.center);
        setScale(1.2F);

        // 初始化为准备状态
        setBirdState(BirdState.ready);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // 飞行或下落状态一直应用物理规律
        if (birdState == BirdState.fly || birdState == BirdState.drop) {
            // 递增速度
            velocityY += Res.Physics.GRAVITY * delta;
            // 递增位移
            setY(getY() + velocityY * delta);
            // 改变头的仰角
            changeBirdRotation(delta);
        }

        //如果在屏幕中就向左移动
        if (birdState == BirdState.die ||  birdState == BirdState.drop) {
            if (getRightX() + getParent().getX() > -10) {
                setX(getX() + Res.Physics.MOVE_VELOCITY * delta);
            }
        }
    }

    int getGrade() {
        return grade;
    }

    public void addGrade(int grade){
        this.grade += grade;
    }

    /**
     * 根据游戏状态刷新小鸟状态
     *
     * @param birdState
     */
    public void setBirdState(BirdState birdState) {
        if (birdState == null || this.birdState == birdState) {
            return;
        }

        this.birdState = birdState;

        switch (this.birdState) {
            case ready:
                // 准备状态循环播放动画, 帧持续时间为 0.2 秒
                setPlayAnimation(true);
                setRotation(0);
                getAnimation().setFrameDuration(0.2F);
                this.grade = 0;
                break;

            case fly:
                // 准备状态循环播放动画, 帧持续时间为 0.18 秒
                setPlayAnimation(true);
                getAnimation().setFrameDuration(0.15F);
                break;

            case die:
            case gameOver:
                // 游戏结束状态停止播放动画, 并固定显示第1帧
                setPlayAnimation(false);
                setFixedShowKeyFrameIndex(1);
                setRotation(-90);
                break;
        }
    }

    public void onTap() {
        this.velocityY = Res.Physics.JUMP_VELOCITY;
    }

    /**
     * 根据数值方向速度变化值改变小鸟的旋转角度
     *
     * @param delta
     */
    private void changeBirdRotation(float delta) {

        float rotation = getRotation();

        rotation += (velocityY * delta);

        if (velocityY > 0) {
            // 向上飞时稍微加大角度旋转的速度
            rotation += (velocityY * delta) * 1.5F;
        } else {
            // 向下飞时稍微减小角度旋转的速度
            rotation += (velocityY * delta) * 0.2F;
        }

        // 校准旋转角度: -75 <= rotation <= 45
        if (rotation < -75) {
            rotation = -75;
        } else if (rotation > 45) {
            rotation = 45;
        }

        // 设置小鸟的旋转角度
        setRotation(rotation);
    }

    public String getImei() {
        return this.imei;
    }

    public boolean isDied(){
        return birdState == BirdState.die;
    }
}













