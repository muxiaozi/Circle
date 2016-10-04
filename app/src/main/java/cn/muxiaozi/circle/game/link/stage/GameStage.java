package cn.muxiaozi.circle.game.link.stage;

import android.graphics.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.game.framwork.BaseStage;
import cn.muxiaozi.circle.game.link.DataFactory;
import cn.muxiaozi.circle.game.link.MainGame;
import cn.muxiaozi.circle.game.link.Res;
import cn.muxiaozi.circle.game.link.actor.DiamondActor;
import cn.muxiaozi.circle.game.link.actor.LinkActor;
import cn.muxiaozi.circle.game.link.actor.PlayerActor;
import cn.muxiaozi.circle.game.link.actor.ReadyActor;
import cn.muxiaozi.circle.game.link.actor.TimeActor;
import cn.muxiaozi.circle.net.DataService;
import cn.muxiaozi.circle.utils.InfoUtil;
import cn.muxiaozi.circle.utils.LogUtil;

/**
 * Created by 慕宵子 on 2016/9/21 0021.
 * <p>
 * 主游戏舞台
 */
public class GameStage extends BaseStage<MainGame> {

    //地图数据，-1代表空，>=0为方块
    private byte[][] map = new byte[Res.COL_NUM][Res.ROW_NUM];

    //连接演员，用于显示两个对象之间的连线
    private LinkActor linkActor;
    //时间演员，用于显示时间
    private TimeActor timeActor;
    //倒计时演员，用于显示倒计时
    private ReadyActor readyActor;
    //玩家管理器
    private PlayerActor playerActor;

    //宝石数组
    private DiamondActor[][] diamonds = new DiamondActor[Res.COL_NUM][Res.ROW_NUM];

    //两点连通路径
    private final List<Point> linkPath = new ArrayList<>(20);

    //初始化每个小格的像素宽度
    private static final float CELL_SIZE_PIXEL = Gdx.graphics.getWidth() / Res.COL_NUM;

    //点击焦点
    private Point focusPoint1 = new Point(-1, -1);
    private Point focusPoint2 = new Point(-1, -1);

    //消除声音
    private Sound pairSound;

    //当前玩家的索引，来自playerList
    private int currentPlayerIndex = -1;

    //自己的IMEI号码
    private int myIndex;

    //是否轮自己操作
    private boolean isTurnMe = false;

    //在哪个玩家身上卡住了
    private int cutPlayer = -1;

    public GameStage(final MainGame game, Viewport viewport, int index) {
        super(game, viewport);

        for (String player : game.getPlayers()) {
            LogUtil.d(player);
        }

        //从资源管理器获取纹理
        TextureAtlas atlas = game.getAssetManager().get(Res.Atlas.ATLAS_PATH, TextureAtlas.class);
        pairSound = game.getAssetManager().get(Res.Audio.PAIR, Sound.class);

        //初始化自己的序号
        this.myIndex = index;

        //初始化连接演员
        linkActor = new LinkActor(new Pixmap((int) game.getWorldWidth(), (int) game.getWorldHeight(),
                Pixmap.Format.RGBA8888));
        addActor(linkActor);

        //初始化时间演员
        timeActor = new TimeActor();
        timeActor.setBounds(0, getHeight() - 50, getWidth(), 50);
        timeActor.setOnTimeListener(new TimeActor.OnTimeListener() {
            @Override
            public void onTimeOut() {
                if (cutPlayer == -1) {
                    cutPlayer = currentPlayerIndex;
                }
                requestTakeTurn();
            }
        });
        addActor(timeActor);

        //添加宝石
        for (int y = 1; y < Res.ROW_NUM - 1; y++) {
            for (int x = 1; x < Res.COL_NUM - 1; x += 2) {
                //第col列
                diamonds[x][y] = new DiamondActor(atlas, (byte) -1);
                diamonds[x][y].setBounds(x * Res.CELL_SIZE + 1, y * Res.CELL_SIZE + 1,
                        Res.CELL_SIZE - 2, Res.CELL_SIZE - 2);
                addActor(diamonds[x][y]);

                //第col+1列
                diamonds[x + 1][y] = new DiamondActor(atlas, (byte) -1);
                diamonds[x + 1][y].setBounds((x + 1) * Res.CELL_SIZE + 1, y * Res.CELL_SIZE + 1,
                        Res.CELL_SIZE - 2, Res.CELL_SIZE - 2);
                addActor(diamonds[x + 1][y]);
            }
        }

        //上下留白
        for (int x = 0; x < Res.COL_NUM; x++) {
            map[x][0] = map[x][Res.ROW_NUM - 1] = -1;
        }

        //左右留白
        for (int y = 0; y < Res.ROW_NUM; y++) {
            map[0][y] = map[Res.COL_NUM - 1][y] = -1;
        }

        playerActor = new PlayerActor(new Pixmap((int) getWidth(), 40, Pixmap.Format.RGB888),
                game.getPlayers().length, myIndex);
        addActor(playerActor);

        //初始化准备演员
        readyActor = new ReadyActor(atlas);
        readyActor.setSize(200, 200);
        readyActor.setCenter(getWidth() / 2, getHeight() / 2);
        addActor(readyActor);
        readyActor.setOnReadyListener(new ReadyActor.OnReadyListener() {
            @Override
            public void onReady(int time) {
                game.send(DataFactory.packReady(time));
                if (time == 0) {
                    createMap();
                    requestTakeTurn();
                }
            }
        });
        if (DataService.isServer()) {
            readyActor.start();
        }
    }

    /**
     * 需要轮流
     */
    private void requestTakeTurn() {
        if (DataService.isServer()) {
            currentPlayerIndex = currentPlayerIndex < getGame().getPlayers().length - 1
                    ? currentPlayerIndex + 1 : 0;
            takeTurn(currentPlayerIndex);
            getGame().send(DataFactory.packTurn(currentPlayerIndex));
        }
    }

    /**
     * 生成地图
     */
    private void createMap() {
        //重设宝石
        for (int y = 1; y < Res.ROW_NUM - 1; y++) {
            for (int x = 1; x < Res.COL_NUM - 1; x += 2) {
                byte type = (byte) MathUtils.random(0, 22);
                setDiamondType(x, y, type);
                setDiamondType(x + 1, y, type);
            }
        }
        upsetMap();
    }

    /**
     * 设置地图每个点
     *
     * @param x    x坐标
     * @param y    y坐标
     * @param type 类型
     */
    private void setDiamondType(int x, int y, byte type) {
        map[x][y] = type;
        diamonds[x][y].setType(type);
    }

    /**
     * 乱序(重置)
     */
    private void upsetMap() {
        clearFocusPoint();
        for (int x = 1; x < Res.COL_NUM - 1; x++) {
            for (int y = 1; y < Res.ROW_NUM - 1; y++) {
                byte tmpType = map[x][y];
                if (tmpType == -1)
                    continue;

                int tmpX = MathUtils.random(1, Res.COL_NUM - 2);
                int tmpY = MathUtils.random(1, Res.ROW_NUM - 2);
                if (map[tmpX][tmpY] == -1)
                    continue;

                setDiamondType(x, y, map[tmpX][tmpY]);
                setDiamondType(tmpX, tmpY, tmpType);
            }
        }

        if (DataService.isServer()) {
            getGame().send(DataFactory.packSyncMap(map));
        }
    }

    /**
     * 提示
     *
     * @param isClick 是否主动提示
     * @return 是否有可用提示
     */
    private boolean showTips(boolean isClick) {
        clearFocusPoint();

        for (int i = 1; i < Res.COL_NUM - 1; i++) {
            for (int j = 1; j < Res.ROW_NUM - 1; j++) {
                if (map[i][j] == -1)
                    continue;

                for (int ii = i; ii < Res.COL_NUM - 1; ii++) {
                    for (int jj = 1; jj < Res.ROW_NUM - 1; jj++) {
                        if (map[i][j] == map[ii][jj]) {
                            if (i == ii && j == jj)
                                continue;
                            focusPoint1.set(i, j);
                            focusPoint2.set(ii, jj);
                            if (canConnected(focusPoint1, focusPoint2, true)) {
                                if (isClick) {
                                    diamonds[focusPoint1.x][focusPoint1.y].setFocus(true);
                                    diamonds[focusPoint2.x][focusPoint2.y].setFocus(true);
                                }
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 竖直方向的连接
     *
     * @param pStart 第一个焦点
     * @param pEnd   第二个焦点
     * @return 如果可以连通返回true，否则返回false
     */
    private boolean connVertical(Point pStart, Point pEnd) {
        if (pStart.x != pEnd.x)
            return false;

        int minY, maxY;

        if (pStart.y < pEnd.y) {
            minY = pStart.y;
            maxY = pEnd.y;
        } else {
            minY = pEnd.y;
            maxY = pStart.y;
        }

        for (int y = minY + 1; y <= maxY - 1; y++) {
            if (map[pStart.x][y] != -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 水平方向的连接
     *
     * @param pStart 第一个焦点
     * @param pEnd   第二个焦点
     * @return 如果可以连通返回true，否则返回false
     */
    private boolean connHorizontal(Point pStart, Point pEnd) {
        if (pStart.y != pEnd.y)
            return false;

        int minX, maxX;

        if (pStart.x < pEnd.x) {
            minX = pStart.x;
            maxX = pEnd.x;
        } else {
            minX = pEnd.x;
            maxX = pStart.x;
        }

        for (int x = minX + 1; x <= maxX - 1; x++) {
            if (map[x][pStart.y] != -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断两点是否可以联通
     *
     * @param pStart  第一个焦点
     * @param pEnd    第二个焦点
     * @param isClick 是否手动点击两个点，如果是，生成路径
     * @return 如果可以联通，返回true，否则返回false
     */
    private boolean canConnected(Point pStart, Point pEnd, boolean isClick) {
        //如果这两个点相同，则不可以联通
        if (pStart.equals(pEnd)) {
            return false;
        }

        // 无拐点
        if (connHorizontal(pStart, pEnd) || connVertical(pStart, pEnd)) {
            if (isClick) {
                linkPath.clear();
                linkPath.add(new Point(pStart));
                linkPath.add(new Point(pEnd));
            }
            return true;
        }

        // 只有一个拐点
        Point pt1 = new Point(pStart.x, pEnd.y);
        if (map[pt1.x][pt1.y] == -1 && connHorizontal(pEnd, pt1) && connVertical(pStart, pt1)) {
            if (isClick) {
                linkPath.clear();
                linkPath.add(new Point(pStart));
                linkPath.add(pt1);
                linkPath.add(new Point(pEnd));
            }
            return true;
        }
        pt1.set(pEnd.x, pStart.y);
        if (map[pt1.x][pt1.y] == -1 && connHorizontal(pStart, pt1) && connVertical(pEnd, pt1)) {
            if (isClick) {
                linkPath.clear();
                linkPath.add(new Point(pStart));
                linkPath.add(pt1);
                linkPath.add(new Point(pEnd));
            }
            return true;
        }

        // 有两个拐点
        Point pt2 = new Point();
        if (pStart.x != pEnd.x) // 列数不同
        {
            // 逐行判断
            for (int y = 0; y < Res.ROW_NUM; y++) {
                if (y != pStart.y && y != pEnd.y) {
                    if (map[pStart.x][y] == -1 && map[pEnd.x][y] == -1) {
                        pt1.set(pStart.x, y);
                        pt2.set(pEnd.x, y);

                        if (connHorizontal(pt1, pt2) && connVertical(pStart, pt1) && connVertical(pEnd, pt2)) {
                            if (isClick) {
                                linkPath.clear();
                                linkPath.add(new Point(pStart));
                                linkPath.add(pt1);
                                linkPath.add(pt2);
                                linkPath.add(new Point(pEnd));
                            }
                            return true;
                        }
                    }
                }
            }
        }

        if (pStart.y != pEnd.y) // 行数不同
        {
            // 逐列判断
            for (int x = 0; x < Res.COL_NUM; x++) {
                if (x != pStart.x && x != pEnd.x) {
                    if (map[x][pStart.y] == -1 && map[x][pEnd.y] == -1) {
                        pt1.set(x, pStart.y);
                        pt2.set(x, pEnd.y);

                        if (connVertical(pt1, pt2) && connHorizontal(pStart, pt1) && connHorizontal(pEnd, pt2)) {
                            if (isClick) {
                                linkPath.clear();
                                linkPath.add(new Point(pStart));
                                linkPath.add(pt1);
                                linkPath.add(pt2);
                                linkPath.add(new Point(pEnd));
                            }
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * 判断是否赢了
     *
     * @return 如果游戏结束，返回true，否则返回false
     */
    private boolean isGameOver() {
        for (int i = 1; i < Res.COL_NUM - 1; i++) {
            for (int j = 1; j < Res.ROW_NUM - 1; j++) {
                if (map[i][j] != -1)
                    return false;
            }
        }
        return true;
    }

    /**
     * 获取触摸点在哪一个方格中
     *
     * @param x 触摸点x坐标
     * @param y 触摸点y坐标
     * @return 方格坐标
     */
    private Point getPointByTouch(int x, int y) {
        int col = (int) (x / CELL_SIZE_PIXEL);
        int row = (int) ((Gdx.graphics.getHeight() - y) / CELL_SIZE_PIXEL);
        row = row > Res.ROW_NUM - 1 ? Res.ROW_NUM - 1 : row;
        return new Point(col, row);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (isTurnMe) {
            Point touchPoint = getPointByTouch(screenX, screenY);
            touchDiamond(touchPoint.x, touchPoint.y);
            getGame().send(DataFactory.packTouch(
                    new DataFactory.TouchEntity(myIndex, touchPoint.x, touchPoint.y)));
        }
        return true;
    }

    private void touchDiamond(int x, int y) {
        if (map[x][y] != -1) {
            //正在进行提示
            if (!focusPoint2.equals(-1, -1)) {
                diamonds[focusPoint1.x][focusPoint1.y].setFocus(false);
                diamonds[focusPoint2.x][focusPoint2.y].setFocus(false);
                focusPoint1.set(-1, -1);
                focusPoint2.set(-1, -1);
            }

            if (focusPoint1.equals(-1, -1)) {
                focusPoint1.set(x, y);
                diamonds[focusPoint1.x][focusPoint1.y].setFocus(true);
            } else {
                focusPoint2.set(x, y);
                if (map[focusPoint1.x][focusPoint1.y] == map[focusPoint2.x][focusPoint2.y] &&
                        canConnected(focusPoint1, focusPoint2, true)) {

                    //清除方块并取消焦点
                    setDiamondType(focusPoint1.x, focusPoint1.y, (byte) -1);
                    setDiamondType(focusPoint2.x, focusPoint2.y, (byte) -1);
                    focusPoint1.set(-1, -1);

                    if (getGame().hasSounds()) {
                        pairSound.play();
                    }
                    linkActor.link(linkPath);

                    //没人被卡住
                    cutPlayer = -1;

                    if (DataService.isServer()) {
                        //检测游戏是否结束
                        if (isGameOver()) {
                            createMap();
                        }

                        // 判断死路则重排
                        while (!showTips(false)) {
                            upsetMap();
                        }
                    }
                } else {
                    diamonds[focusPoint1.x][focusPoint1.y].setFocus(false);
                    focusPoint1.set(x, y);
                    diamonds[focusPoint1.x][focusPoint1.y].setFocus(true);
                }
                focusPoint2.set(-1, -1);
            }
        }
    }

    /**
     * 清除焦点
     */
    private void clearFocusPoint() {
        if (!focusPoint1.equals(-1, -1)) {
            diamonds[focusPoint1.x][focusPoint1.y].setFocus(false);
            focusPoint1.set(-1, -1);
        }
        if (!focusPoint2.equals(-1, -1)) {
            diamonds[focusPoint2.x][focusPoint2.y].setFocus(false);
            focusPoint2.set(-1, -1);
        }
    }

    private void takeTurn(int index) {
        currentPlayerIndex = index;

        playerActor.takeTurn(index);

        clearFocusPoint();
        isTurnMe = myIndex == index;
        timeActor.start();

        if (cutPlayer == currentPlayerIndex) {
            showTips(true);
        }
    }

    @Override
    public void receive(final byte[] data) {
        switch (data[0]) {
            case DataFactory.TYPE_TOUCH:
                DataFactory.TouchEntity entity = DataFactory.unpackTouch(data);
                if (entity != null) {
                    touchDiamond(entity.x, entity.y);
                }
                break;

            case DataFactory.TYPE_TURN:
                takeTurn(DataFactory.unpackTurn(data));
                break;

            case DataFactory.TYPE_READY:
                readyActor.setValue(DataFactory.unpackReady(data));
                break;

            case DataFactory.TYPE_SYNC_MAP:
                byte[][] map = DataFactory.unpackSyncMap(data);
                if (map != null) {
                    GameStage.this.map = map;
                    for (int i = 1; i < Res.COL_NUM - 1; i++) {
                        for (int j = 1; j < Res.ROW_NUM - 1; j++) {
                            diamonds[i][j].setType(map[i][j]);
                        }
                    }
                    clearFocusPoint();
                }
                break;
        }
    }
}
