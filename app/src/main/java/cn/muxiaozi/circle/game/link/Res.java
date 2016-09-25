package cn.muxiaozi.circle.game.link;

/**
 * Created by 慕宵子 on 2016/9/22 0022.
 * <p>
 * 连连看配置
 */

public interface Res {

    /**
     * 固定世界宽度, 高度根据实际屏幕比例换算
     */
    float FIX_WORLD_WIDTH = 600;

    /**
     * 列数
     */
    int COL_NUM = 8;

    /**
     * 行数
     */
    int ROW_NUM = 14;

    /**
     * 每个宝石的尺寸(宽:高 = 1:1)
     */
    float CELL_SIZE = FIX_WORLD_WIDTH / COL_NUM;

    /**
     * 显示连线的时间
     */
    float LINK_INTERVAL = 0.5F;

    /**
     * 纹理资源
     */
    interface Atlas {
        String ATLAS_PATH = "link/atlas/link.atlas";

        //宝石名
        String DIAMOND = "diamond";

        //数字
        String GRADE = "grade";

        //Combo
        String COMBO = "combo";
    }


    /**
     * 音频资源
     */
    interface Audio {
        //选择
        String PAIR = "link/audio/pair.ogg";

        String AMAZING = "link/audio/raw_amazing.mp3";

        String EXCELLENT = "link/audio/raw_excellent.mp3";

        String GOOD = "link/audio/raw_good.mp3";

        String GREAT = "link/audio/raw_great.mp3";

        String UNBELIEVABLE = "link/audio/raw_unbelievable.mp3";
    }
}
