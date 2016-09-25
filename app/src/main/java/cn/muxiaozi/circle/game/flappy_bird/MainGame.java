package cn.muxiaozi.circle.game.flappy_bird;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.game.flappy_bird.screen.LoadScreen;
import cn.muxiaozi.circle.game.flappy_bird.screen.GameScreen;
import cn.muxiaozi.circle.game.framwork.BaseGame;
import cn.muxiaozi.circle.net.DataFactory;


/**
 * 游戏主程序入口类
 *
 * @xietansheng
 */
public class MainGame extends BaseGame {

    public static final String TAG = "FlappyBird";

    /**
     * 是否显示帧率
     */
    private boolean showFps;

    /**
     * 世界宽度
     */
    private float worldWidth;
    /**
     * 世界高度
     */
    private float worldHeight;

    /**
     * 资源管理器
     */
    private AssetManager assetManager;

    /**
     * 纹理图集
     */
    private TextureAtlas atlas;

    /**
     * 加载游戏场景
     */
    private LoadScreen loadScreen;

    /**
     * 主游戏场景
     */
    private GameScreen gameScreen;

    /**
     * 用于调试显示帧率
     */
    private FPSDebug fpsDebug;

    private SoundsManager soundsManager;

    public MainGame(String[] players) {
        super(players);
    }

    @Override
    public void create() {
        // 设置 LOG 输出级别
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        // 为了不压扁或拉长图片, 按实际屏幕比例计算世界宽高
        worldHeight = Res.FIX_WORLD_HEIGHT;
        worldWidth = Gdx.graphics.getWidth() * worldHeight / Gdx.graphics.getHeight();

        Gdx.app.log(TAG, "World Size: " + worldWidth + " * " + worldHeight);

        // 创建资源管理器
        assetManager = new AssetManager();

        // 加载资源
        assetManager.load(Res.Atlas.ATLAS_PATH, TextureAtlas.class);

        assetManager.load(Res.Audios.AUDIO_DIE, Sound.class);
        assetManager.load(Res.Audios.AUDIO_HIT, Sound.class);
        assetManager.load(Res.Audios.AUDIO_TOUCH, Sound.class);
        assetManager.load(Res.Audios.AUDIO_RESTART, Sound.class);
        assetManager.load(Res.Audios.AUDIO_SCORE, Sound.class);

        // 等待资源加载完毕
        assetManager.finishLoading();

        // 初始化音效管理器
        soundsManager = new SoundsManager(assetManager);

        // 获取资源
        atlas = assetManager.get(Res.Atlas.ATLAS_PATH, TextureAtlas.class);

        // 创建加载游戏场景
        loadScreen = new LoadScreen(this);
        if (getPlayers() != null)
            loadScreen.setPlayerList(getPlayers());

        // 设置当前场景
        setScreen(loadScreen);

        // 判断是否需要显示帧率, 如果需要, 则进行初始化
        showFps = Gdx.app.getPreferences(Constants.CIRCLE_CONFIG)
                .getBoolean(Constants.ConfigOption.FPS, false);
        if (showFps) {
            fpsDebug = new FPSDebug();
            fpsDebug.init(new BitmapFont(), 24);
        }
    }

    public SoundsManager getSoundsManager() {
        return soundsManager;
    }

    /**
     * 开始游戏
     *
     * @param players
     */
    public void startGame(String[] players) {
        gameScreen = new GameScreen(this, players);
        setScreen(gameScreen);
    }

    @Override
    public void render() {
        // 黑色清屏
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 父类渲染场景
        super.render();

        // 判断是否需要渲染帧率
        if (showFps) {
            fpsDebug.render();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        // 应用退出时需要手动销毁场景
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        // 应用退出时释放资源
        if (assetManager != null) {
            assetManager.dispose();
        }
        if (showFps) {
            fpsDebug.dispose();
        }
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    /**
     * 用于调试显示帧率
     */
     private class FPSDebug implements Disposable {

        private SpriteBatch batch;

        private BitmapFont fpsBitmapFont;

        /**
         * 文本高度占屏幕高度的比例
         */
        private static final float OCCUPY_HEIGHT_RATIO = 14.0F / 480.0F;

        /**
         * 显示的文本偏移右下角的X轴和Y轴比例(相对于字体高度的比例)
         */
        private static final float DISPLAY_ORIGIN_OFFSET_RATIO = 0.5F;

        // 帧率字体绘制的位置
        private float x;
        private float y;

        void init(BitmapFont fpsBitmapFont, int fontRawPixSize) {
            this.batch = new SpriteBatch();
            this.fpsBitmapFont = fpsBitmapFont;
            // 计算帧率文本显示位置（为了兼容所有不同尺寸的屏幕）
            float height = Gdx.graphics.getHeight();
            float scale = (height * OCCUPY_HEIGHT_RATIO) / (float) fontRawPixSize;
            this.fpsBitmapFont.getData().setScale(scale);
            float scaledFontSize = fontRawPixSize * scale;
            float offset = scaledFontSize * DISPLAY_ORIGIN_OFFSET_RATIO;
            x = scaledFontSize - offset;
            y = scaledFontSize * 1.85F - offset;
        }

        void render() {
            // 绘制帧率
            batch.begin();
            fpsBitmapFont.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), x, y);
            batch.end();
        }

        @Override
        public void dispose() {
            // 销毁 batch
            if (batch != null) {
                batch.dispose();
                batch = null;
            }
            fpsBitmapFont.dispose();
        }
    }

    @Override
    public boolean receive(byte[] data) {
        if (data[0] == DataFactory.TYPE_DISCONNECT_SERVER) {
            Gdx.app.exit();
        }
        return super.receive(data);
    }
}
















