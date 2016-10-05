package cn.muxiaozi.circle.game.flappy_bird.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Iterator;

import cn.muxiaozi.circle.game.flappy_bird.BirdState;
import cn.muxiaozi.circle.game.flappy_bird.DataFactory;
import cn.muxiaozi.circle.game.flappy_bird.MainGame;
import cn.muxiaozi.circle.game.flappy_bird.Res;
import cn.muxiaozi.circle.game.flappy_bird.actor.BarActor;
import cn.muxiaozi.circle.game.flappy_bird.actor.BirdGroup;
import cn.muxiaozi.circle.game.flappy_bird.actor.FloorActor;
import cn.muxiaozi.circle.game.flappy_bird.actor.NumGroup;
import cn.muxiaozi.circle.game.flappy_bird.actor.ReadyActor;
import cn.muxiaozi.circle.game.framwork.BaseActor;
import cn.muxiaozi.circle.game.framwork.BaseStage;
import cn.muxiaozi.circle.game.utils.CollisionUtils;
import cn.muxiaozi.circle.net.DataService;


/**
 * 主游戏舞台（主要的游戏逻辑都在这里）
 *
 * @xietansheng
 */
public class GameStage extends BaseStage<MainGame> {

    /**
     * 背景
     */
    private BaseActor bgActor;

    /**
     * 地板
     */
    private FloorActor floorActor;

    /**
     * 准备状态提示
     */
    private ReadyActor getReadyActor;

    /**
     * 小鸟组
     */
    private BirdGroup birdGroup;

    /**
     * 大数字的分数显示
     */
    private NumGroup bigScoreActor;

    /**
     * 当前界面可见的所有水管集合
     */
    private final ArrayList<BarActor> barActorList = new ArrayList<>();
    /**
     * 水管对象缓存池, 因为水管需要频繁生成和移除, 所有使用对象池减少对象的频繁创建
     */
    private Pool<BarActor> barActorPool;

    /**
     * 距离下次生成水管的时间累加器
     */
    private float generateBarTimeCounter;

    /**
     * 死掉后作为观众，每隔一定时间变换一次焦点
     */
    private float audienceTimeCounter;

    /**
     * 游戏状态
     */
    private BirdState birdState;

    /**
     * 玩家列表
     */
    private String[] players;

    /**
     * 地图宽度
     */
    private float mapWidth;


    /**
     * 碰撞到水管的音效
     */
    private Sound hitSound;

    /**
     * 得分音效
     */
    private Sound scoreSound;

    /**
     * 点击屏幕时播放的音效
     */
    private Sound touchSound;

    /**
     * 撞到地板时的音效
     */
    private Sound dieSound;


    public GameStage(MainGame mainGame, Viewport viewport, String[] players) {
        super(mainGame, viewport);
        this.players = players;

        //根据小鸟的数量计算世界的宽度
        TextureAtlas.AtlasRegion bird = mainGame.getAtlas().findRegion(Res.Atlas.IMAGE_BIRD_YELLOW_01_TO_03, 1);
        this.mapWidth = (bird.getRegionWidth() + Res.Physics.BIRD_INTERVAL) *
                (players.length - 1) + mainGame.getWorldWidth();

        init();
    }

    private void init() {
        //初始化音频
        hitSound = getGame().getAssetManager().get(Res.Audios.AUDIO_HIT, Sound.class);
        scoreSound = getGame().getAssetManager().get(Res.Audios.AUDIO_SCORE, Sound.class);
        touchSound = getGame().getAssetManager().get(Res.Audios.AUDIO_TOUCH, Sound.class);
        dieSound = getGame().getAssetManager().get(Res.Audios.AUDIO_DIE, Sound.class);

        /*
         * 创建背景
         */
        bgActor = new BaseActor(getGame().getAtlas().findRegion(Res.Atlas.IMAGE_GAME_BG));
        // 位置设置到舞台中心
        bgActor.setCenterY(getHeight() / 2);
        addActor(bgActor);

        /*
         * 创建地板
         */
        floorActor = new FloorActor(getGame());
        // 设置地板移动速度
        floorActor.setMoveVelocity(Res.Physics.MOVE_VELOCITY);
        // 水平居中
        // 兼容性设置纵坐标（为了兼容不同尺寸的屏幕）
        floorActor.setTopY(150);
        addActor(floorActor);

        /*
         * 创建大数字分数显示
         */
        bigScoreActor = new NumGroup(getGame(), Res.Atlas.IMAGE_NUM_BIG_00_TO_09);
        bigScoreActor.setTopY(getHeight() - 50);
        addActor(bigScoreActor);

        /*
         * 创建小鸟
         */
        birdGroup = new BirdGroup(getGame(), this.players);
        birdGroup.setRightX(mapWidth - getGame().getWorldWidth() * 0.6F);
        addActor(birdGroup);

        // 将地板设置到小鸟前面(ZIndex 必须在对象添加到舞台后设置才有效, ZIndex 越大显示越前)
        floorActor.setZIndex(birdGroup.getZIndex());
        // 分数显示排在最前面
        bigScoreActor.setZIndex(getRoot().getChildren().size - 1);

        /*
         * 创建 准备提示
         */
        getReadyActor = new ReadyActor(getGame());
        getReadyActor.setTopY(getHeight() / 2);
        addActor(getReadyActor);
        getReadyActor.setZIndex(getRoot().getChildren().size - 1);

		/*
         * 水管
		 */
        // 获取水管对象池(BarActor 中必须有空参构造方法用于给 Pool 通过反射实例化对象)
        // 注意: Pools.get() 方式获取的对象池会使用到反射, 而 html 平台是通过 GWT 实现的,
        // GWT 不支持反射, 因此要想能发布到 html 平台, 需要自己手动继承 Pool 抽象类实现对象池。
        barActorPool = Pools.get(BarActor.class, 14);

		/*
         * 初始为游戏准备状态
		 */
        ready();
    }

    /**
     * 游戏状态改变方法01: 游戏准备状态
     */
    public void ready() {
        birdState = BirdState.ready;

        // 设置小鸟初始Y轴坐标
        birdGroup.ready();
        focusBird(birdGroup.getMyImei());

        // 地板停止移动
        floorActor.setMove(false);

        // 清空水管
        for (BarActor barActor : barActorList) {
            // 从舞台中移除水管
            getRoot().removeActor(barActor);
        }
        // 从集合中移除水管
        barActorList.clear();

        // 分数清零
        bigScoreActor.setVisible(false);
        bigScoreActor.setNum(0);
        // 更新分数后重新水平居中
        bigScoreActor.setCenterX(getCamera().position.x);

        // 设置点击提示和准备提示可见
        getReadyActor.setVisible(true);
        getReadyActor.setNum(3);
        if (DataService.isServer()) {
            getReadyActor.startReady(new ReadyActor.OnReadyListener() {
                @Override
                public void update(int time) {
                    getGame().send(DataFactory.packReady(time));
                    if (time == 0) {
                        startGame();
                    } else {
                        getReadyActor.setNum(time);
                    }
                }
            });
        }
    }

    /**
     * 游戏状态方法02: 开始游戏
     */
    private void startGame() {
        birdState = BirdState.fly;

        // 刷新小鸟显示帧和旋转角度
        birdGroup.fly();

        // 地板开始移动
        floorActor.setMove(true);

        // 隐藏提示
        getReadyActor.setVisible(false);

        //显示分数
        bigScoreActor.setVisible(true);

        // 生成水管时间计数器清零
        generateBarTimeCounter = 0.0F;

        // 观众模式计数器清零
        audienceTimeCounter = 0.0F;
    }

    /**
     * 游戏结束，所有人游戏结束
     */
    private void gameOver(int[] grades) {
        birdState = BirdState.gameOver;

        // 刷新小鸟显示帧和旋转角度
        birdGroup.gameOver();

        // 地板停止移动
        floorActor.setMove(false);

        // 停止移动所有水管
        for (BarActor barActor : barActorList) {
            if (barActor.isMove()) {
                barActor.setMove(false);
            }
        }

        // 显示游戏结束舞台
        getGame().getGameScreen().showGameOverStage(bigScoreActor.getNum());
    }

    /**
     * 随机生成一对水管
     */
    private void generateBar(float downBarTopY) {
        // 创建下方水管（从对象池中获取）
        BarActor downBarActor = barActorPool.obtain();
        downBarActor.setMainGame(getGame());
        downBarActor.setUpBar(false);
        downBarActor.setX(mapWidth);
        downBarActor.setTopY(downBarTopY + floorActor.getTopY());
        downBarActor.setMoveVelocity(Res.Physics.MOVE_VELOCITY);
        // 创建后水管立即开始移动
        downBarActor.setMove(true);
        addActor(downBarActor);
        // 将水管加入到集合, 方便进行碰撞检测
        barActorList.add(downBarActor);
        // 将水管设置到小鸟后面(必须在 actor 添加到 stage 后设置 ZIndex 才有效)
        downBarActor.setZIndex(floorActor.getZIndex());

        // 创建上方水管
        BarActor upBarActor = barActorPool.obtain();
        upBarActor.setMainGame(getGame());
        upBarActor.setUpBar(true);
        upBarActor.setX(mapWidth);
        upBarActor.setY(downBarActor.getTopY() + Res.Physics.BAR_INTERVAL);
        upBarActor.setMoveVelocity(Res.Physics.MOVE_VELOCITY);
        upBarActor.setMove(true);
        addActor(upBarActor);
        barActorList.add(upBarActor);
        upBarActor.setZIndex(floorActor.getZIndex());
    }

    /**
     * 逻辑校验(碰撞检测, 得分检测, 移除水管)
     */
    private void checkLogic() {
        // 正在飞翔状态时才判断是否碰撞到水管或通过水管
        if (birdState == BirdState.fly) {
            for (BarActor barActor : barActorList) {
                if (CollisionUtils.isCollision(birdGroup.getMyRect(), barActor,
                        Res.Physics.DEPTH)) {
                    Gdx.app.log(MainGame.TAG, "Collision Bar.");

                    if (getGame().hasSounds()) {
                        hitSound.play();
                    }
                    birdState = BirdState.die;
                    birdGroup.getMyBird().setBirdState(BirdState.drop);
                    getGame().send(DataFactory.packDie(birdGroup.getMyImei()));
                    break;
                }

                // 小鸟通过上方水管的右边, 则认为已通过水管, 增加分数 (上下两条水管只需要检测一个, 因为水管是一对的, X 轴坐标相同)
                if (!barActor.isPassByBird() && barActor.isUpBar() &&
                        birdGroup.getMyBird().getX() + birdGroup.getX() > barActor.getRightX()) {
                    bigScoreActor.addNum(1);
                    bigScoreActor.setCenterX(getCamera().position.x);

                    birdGroup.getMyBird().addGrade(1);
                    if (getGame().hasSounds()) {
                        scoreSound.play();
                    }
                    barActor.setPassByBird(true);

                    getGame().send(DataFactory.packOverBar(birdGroup.getMyImei()));
                    Gdx.app.log(MainGame.TAG, "Score: " + bigScoreActor.getNum());
                }
            }
        }

        // 移除移动出屏幕外的水管
        Iterator<BarActor> it = barActorList.iterator();
        BarActor barActor;
        while (it.hasNext()) {
            barActor = it.next();
            if (barActor.getRightX() < 0) {
                // 从舞台中移除水管
                getRoot().removeActor(barActor);
                // 从集合中移除水管
                it.remove();
                // 回收水管对象(放回到对象池中)
                barActorPool.free(barActor);
            }
        }

        // 判断小鸟是否撞到地板
        if (birdGroup.collision(floorActor.getTopY())) {
            if (!birdGroup.getMyBird().isDied()) {
                Gdx.app.log(MainGame.TAG, "Collision Floor.");
                if (getGame().hasSounds()) {
                    dieSound.play();
                }
                birdState = BirdState.die;
                birdGroup.getMyBird().setBirdState(BirdState.die);
            }

            if (birdGroup.isAllBirdDied() && DataService.isServer()) {
                getGame().send(DataFactory.packGameOver(birdGroup.getGrades()));
                gameOver(birdGroup.getGrades());
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // 小鸟处于飞翔状态(没有碰撞到水管和地板) 或 死亡状态(已撞到水管但还未掉落到地面)时进行逻辑检测
        if (birdState == BirdState.fly || birdState == BirdState.die) {
            // 逻辑检测(碰撞检测, 得分检测, 移除已移动出屏幕的水管)
            checkLogic();
        }

        if (birdState == BirdState.die) {
            audienceTimeCounter += delta;

            if (audienceTimeCounter >= Res.Physics.AUDIENCE_TIME_INTERVAL) {
                focusBird(birdGroup.getAliveBirdByRandom());
                audienceTimeCounter = 0.0F;
            }
        }

        // 正在飞翔状态或者死亡状态执行生成水管的逻辑
        // 死亡并不代表游戏结束，有可能其他玩家还在飞翔
        if (DataService.isServer()) {
            if (birdState == BirdState.fly || birdState == BirdState.die) {
                // 累计下一次水管生成时间
                generateBarTimeCounter += delta;

                // 累计值达到水管生成间隔时间后生成一对水管
                if (generateBarTimeCounter >= Res.Physics.GENERATE_BAR_TIME_INTERVAL) {
                    float downBarTopY = MathUtils.random(140, 320);
                    generateBar(downBarTopY);
                    getGame().send(DataFactory.packGenerateBar(downBarTopY));

                    // 清零累计变量, 重新累计下一次水管生成时间
                    generateBarTimeCounter = 0;
                }
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        barActorList.clear();
        birdGroup.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (birdState == BirdState.fly) {
            // 小鸟正在飞翔状态(没有碰撞到水管和地板), 并且没有飞出屏幕上方, 则响应屏幕触摸给小鸟设置一个向上的速度
            if (birdGroup.getMyBird().getTopY() < getHeight()) {
                birdGroup.getMyBird().onTap();
                // 播放触摸屏幕音效
                if (getGame().hasSounds()) {
                    touchSound.play();
                }

                getGame().send(DataFactory.packJump(new DataFactory.JumpEntity(
                        birdGroup.getMyImei(), birdGroup.getMyBird().getY()
                )));
            }
        }

        return true;
    }

    @Override
    public void receive(final byte[] data) {
        String imei;
        switch (data[0]) {
            case DataFactory.TYPE_JUMP:
                DataFactory.JumpEntity jumpEntity = DataFactory.unpackJump(data);
                birdGroup.onTap(jumpEntity.imei, jumpEntity.y);
                break;

            case DataFactory.TYPE_OVER_BAR:
                imei = DataFactory.unpackOverBar(data);
                birdGroup.addGrade(imei);
                break;

            case DataFactory.TYPE_GENERATE_BAR:
                generateBar(DataFactory.unpackGenerateBar(data));
                break;

            case DataFactory.TYPE_DIE:
                imei = DataFactory.unpackDie(data);
                birdGroup.setState(imei, BirdState.drop);

                //如果所有小鸟都死亡并且自己是服务器
                if (birdGroup.isAllBirdDied() && DataService.isServer()) {
                    getGame().send(DataFactory.packGameOver(birdGroup.getGrades()));
                }
                break;

            case DataFactory.TYPE_GAME_OVER:
                gameOver(DataFactory.unpackGameOver(data));
                break;

            case DataFactory.TYPE_READY:
                int time = DataFactory.unpackReady(data);
                if (time == 0) {
                    startGame();
                } else {
                    getReadyActor.setNum(time);
                }
                break;
        }
    }

    /**
     * 聚焦一直小鸟
     *
     * @param imei
     */
    private void focusBird(String imei) {
        int index;
        for (index = 0; index < players.length; index++) {
            if (players[index].equals(imei)) {
                index = players.length - index - 1;
                break;
            }
        }

        getCamera().position.x = this.mapWidth - getGame().getWorldWidth() / 2
                - index * birdGroup.getMargin();

        bgActor.setCenterX(getCamera().position.x);
        floorActor.setCenterX(getCamera().position.x);
        getReadyActor.setCenterX(getCamera().position.x);
        bigScoreActor.setCenterX(getCamera().position.x);
    }
}


















