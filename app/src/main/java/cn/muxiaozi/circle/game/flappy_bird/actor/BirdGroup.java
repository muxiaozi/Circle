package cn.muxiaozi.circle.game.flappy_bird.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;

import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.game.flappy_bird.MainGame;
import cn.muxiaozi.circle.game.flappy_bird.BirdState;
import cn.muxiaozi.circle.game.flappy_bird.Res;
import cn.muxiaozi.circle.game.framwork.BaseGroup;
import cn.muxiaozi.circle.utils.InfoUtil;

/**
 * Created by 慕宵子 on 2016/8/11 0011.
 * Email: 1002042998@qq.com
 */
public class BirdGroup extends BaseGroup<MainGame> implements Disposable {

    //小鸟与名字之间的间隔
    private static final int NAME_MARGIN = 10;

    //玩家信息列表
    private final String[] players;

    //玩家名字标签数组
    private final Label[] nameLabels;

    //小鸟数组
    private final BirdActor[] birds;

    //玩家的数量
    private final int size;

    //自己小鸟，指向小鸟数组中自己的指针
    private BirdActor myBird = null;

    private String myImei;

    private BitmapFont nameBitmapFont;

    public BirdGroup(MainGame game, String[] players) {
        super(game);
        //初始化自己的IMEI信息
        this.myImei = Gdx.app.getPreferences(Constants.CIRCLE_CONFIG).getString(InfoUtil.IMEI);

        this.players = players;

        //初始化姓名标签样式
        nameBitmapFont = new BitmapFont();
        Label.LabelStyle labelStyle = new Label.LabelStyle(nameBitmapFont, null);
        labelStyle.font.getData().setScale(1.5F);

        //初始化
        this.size = this.players.length;
        this.birds = new BirdActor[this.size];
        this.nameLabels = new Label[this.size];
        for (int i = 0; i < this.size; i++) {
            //生成小鸟
            birds[i] = new BirdActor(game, this.players[i]);
            addActor(birds[i]);

            //生成姓名标签
            nameLabels[i] = new Label("  0", labelStyle);
            if (this.players[i].equals(myImei)) {
                nameLabels[i].setColor(Color.RED);
            } else {
                nameLabels[i].setColor(Color.BLACK);

            }
            addActor(nameLabels[i]);
        }

        setSize(birds[0].getWidth() * size + Res.Physics.BIRD_INTERVAL * (size - 1),
                game.getWorldHeight());
    }

    public void ready() {
        for (int i = 0; i < size; i++) {
            birds[i].setX(i * (birds[0].getWidth() + Res.Physics.BIRD_INTERVAL));
            birds[i].setY(getGame().getWorldHeight() / 2);
            birds[i].setBirdState(BirdState.ready);
            birds[i].onTap();
        }
    }

    public void fly() {
        for (BirdActor bird : birds) {
            bird.setBirdState(BirdState.fly);
        }
    }

    public void gameOver() {
        for (BirdActor bird : birds) {
            bird.setBirdState(BirdState.gameOver);
        }
    }

    /**
     * 为小鸟设置状态
     *
     * @param imei  被设置状态的小鸟
     * @param state 状态
     */
    public void setState(String imei, BirdState state) {
        for (BirdActor bird : birds) {
            if (bird.getImei().equals(imei)) {
                bird.setBirdState(state);
                break;
            }
        }
    }

    /**
     * 增加分数
     *
     * @param imei
     */
    public void addGrade(String imei) {
        for (BirdActor bird : birds) {
            if (bird.getImei().equals(imei)) {
                bird.addGrade(1);
                break;
            }
        }
    }

    /**
     * 小鸟们是否碰到地面
     *
     * @return 如果自己碰到地面则返回true，否则返回false
     */
    public boolean collision(float topY) {
        for (BirdActor bird : birds) {
            if (bird.getY() - topY < Res.Physics.DEPTH) {
                if (bird.getImei().equals(myImei)) {
                    //如果是自己，处理转移到舞台中去
                    return true;
                } else {
                    bird.setBirdState(BirdState.die);
                }
            }
        }
        return false;
    }

    /**
     * 小鸟飞起
     *
     * @param imei 小鸟的编号
     */
    public void onTap(String imei, float y) {
        if (imei == null) return;

        for (BirdActor bird : birds) {
            if (bird.getImei().equals(imei)) {
                bird.onTap();
                bird.setY(y);
                break;
            }
        }
    }

    /**
     * 获取自己的小鸟
     *
     * @return
     */
    public BirdActor getMyBird() {
        if (myBird == null) {
            for (BirdActor bird : birds) {
                if (bird.getImei().equals(myImei)) {
                    myBird = bird;
                    break;
                }
            }
        }
        return myBird;
    }

    /**
     * 获取自己小鸟的包围矩形用于碰撞检测
     *
     * @return 矩形
     */
    public Rectangle getMyRect() {
        BirdActor bird = getMyBird();
        if (myBird == null) return null;

        Rectangle rect = new Rectangle();
        rect.setSize(
                bird.getWidth() * bird.getScaleX(),
                bird.getHeight() * bird.getScaleY()
        );
        rect.setPosition(
                bird.getX() + getX() - (bird.getOriginX() * bird.getScaleX() - bird.getOriginX()),
                bird.getY() - (bird.getOriginY() * bird.getScaleY() - bird.getOriginY())
        );
        return rect;
    }

    /**
     * 两只小鸟之间的间距
     *
     * @return 距离（包括小鸟的宽度）
     */
    public float getMargin() {
        return Res.Physics.BIRD_INTERVAL + birds[0].getWidth();
    }

    /**
     * 获取小鸟们的分数
     *
     * @return
     */
    public int[] getGrades() {
        int[] grades = new int[size];
        for (int i = 0; i < size; i++) {
            grades[i] = birds[i].getGrade();
        }
        return grades;
    }

    /**
     * 获取自己的IMEI
     *
     * @return
     */
    public String getMyImei() {
        return myImei;
    }

    /**
     * 检测是否所有小鸟都阵亡
     *
     * @return
     */
    public boolean isAllBirdDied() {
        for (BirdActor bird : birds) {
            if (!bird.isDied())
                return false;
        }
        return true;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        //设置姓名标签的位置，以便跟随移动
        for (int i = 0; i < size; i++) {
            nameLabels[i].setText("  " + birds[i].getGrade());
            nameLabels[i].setPosition(birds[i].getX(),
                    birds[i].getY() + birds[i].getHeight() + NAME_MARGIN);
        }
    }

    /**
     * 随机返回一只活着的小鸟
     *
     * @return
     */
    public String getAliveBirdByRandom() {
        ArrayList<String> aliveBirds = new ArrayList<>(size);
        for (BirdActor bird : birds) {
            if (!bird.isDied()) {
                aliveBirds.add(bird.getImei());
            }
        }

        try {
            int rand = (int) (Math.random() * aliveBirds.size());
            return aliveBirds.get(rand);
        } catch (Exception e) {
            e.printStackTrace();
            return getMyImei();
        }
    }

    @Override
    public void dispose() {
        nameBitmapFont.dispose();
    }
}
