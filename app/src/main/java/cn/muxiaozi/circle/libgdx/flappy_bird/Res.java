package cn.muxiaozi.circle.libgdx.flappy_bird;

/**
 * 资源常量
 *
 * @xietansheng
 */
public interface Res {

    /**
     * 固定世界高度, 宽度根据实际屏幕比例换算
     */
    float FIX_WORLD_HEIGHT = 800;

    /**
     * 相关物理参数（调节物理参数可改变游戏难度）
     */
    interface Physics {

        /**
         * 水管和地板的移动速度, 单位: px/s
         */
        float MOVE_VELOCITY = -150.0F;

        /**
         * 点击屏幕时给小鸟的竖直方向上的速度, 单位: px/s
         */
        float JUMP_VELOCITY = 320.0F;

        /**
         * 小鸟竖直方向上的重力加速度, 单位: px/(s*s)
         */
        float GRAVITY = -520.0F;

        /**
         * 撞入深度, 小鸟撞入水管或地板该深度后才算碰撞
         */
        float DEPTH = 0.0F;

        /**
         * 生成水管时间间隔, 单位: s
         */
        float GENERATE_BAR_TIME_INTERVAL = 2.2F;

        /**
         * 切换观众模式聚焦的时间
         */
        float AUDIENCE_TIME_INTERVAL = 3.0F;

        /**
         * 上下水管之间的间隔
         */
        float BAR_INTERVAL = 180.0F;

        /**
         * 两只小鸟之间的间隔
         */
        float BIRD_INTERVAL = 60.0F;
    }

    /**
     * 纹理图集
     */
    interface Atlas {

        /**
         * 纹理图集 文件路径
         */
        String ATLAS_PATH = "flappybird/atlas/flappybird.atlas";

        /* 纹理图集中的小图名称 */
        String IMAGE_GAME_BG = "game_bg";
        String IMAGE_GAME_FLOOR = "game_floor";
        String IMAGE_GAME_RESULT_BG = "game_result_bg";
        String IMAGE_BAR_DOWN = "bar_down";
        String IMAGE_BAR_UP = "bar_up";
        String IMAGE_GAME_TAP_TIP = "game_tap_tip";
        String IMAGE_GAME_READY = "game_ready";
        String IMAGE_GAME_OVER = "game_over";

        String IMAGE_GAME_START_01_TO_02 = "game_start";
        String IMAGE_GAME_MEDAL_01_TO_04 = "game_medal";
        String IMAGE_NUM_BIG_00_TO_09 = "num_big";
        String IMAGE_NUM_SCORE_00_TO_09 = "num_score";
        String IMAGE_BIRD_YELLOW_01_TO_03 = "bird_yellow";
    }

    /**
     * 音效
     */
    interface Audios {

        /**
         * 音效资源文件夹路径
         */
        String AUDIO_BASE_DIR = "flappybird/audio/";

        // 音效资源路径
        String AUDIO_DIE = AUDIO_BASE_DIR + "die.ogg";
        String AUDIO_HIT = AUDIO_BASE_DIR + "hit.ogg";
        String AUDIO_TOUCH = AUDIO_BASE_DIR + "touch.ogg";
        String AUDIO_RESTART = AUDIO_BASE_DIR + "restart.ogg";
        String AUDIO_SCORE = AUDIO_BASE_DIR + "score.ogg";
    }

    /**
     * Preferences 本地存储相关
     */
    interface Prefs {

        String PREFS_FILE_NAME = "prefs_flappy_bird";

        String KEY_BEST_SCORE = "best_score";
    }

}




















