package cn.muxiaozi.circle.game.link;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import cn.muxiaozi.circle.game.framwork.BaseGame;
import cn.muxiaozi.circle.game.link.screen.GameScreen;
import cn.muxiaozi.circle.utils.LogUtil;

/**
 * Created by 慕宵子 on 2016/9/18 0018.
 *
 * 游戏主程序
 */
public class MainGame extends BaseGame{

    //世界的宽高
    private float worldWidth;
    private float worldHeight;

    //资源管理器
    private AssetManager assetManager;

    public MainGame(String[] players) {
        super(players);
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_INFO);

        //根据玩家数量决定地图大小
        worldWidth = Res.FIX_WORLD_WIDTH;
        worldHeight = worldWidth * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

        assetManager = new AssetManager();
        //加载纹理
        assetManager.load(Res.Atlas.ATLAS_PATH, TextureAtlas.class);
        //加载音频
        assetManager.load(Res.Audio.PAIR, Sound.class);
        assetManager.load(Res.Audio.AMAZING, Sound.class);
        assetManager.load(Res.Audio.EXCELLENT, Sound.class);
        assetManager.load(Res.Audio.GOOD, Sound.class);
        assetManager.load(Res.Audio.GREAT, Sound.class);
        assetManager.load(Res.Audio.UNBELIEVABLE, Sound.class);

        //等待资源加载完毕
        assetManager.finishLoading();

        GameScreen gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    @Override
    public void render() {
        // 白色清屏
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //父类场景渲染
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        assetManager.dispose();
        LogUtil.i("dispose");
    }
}
