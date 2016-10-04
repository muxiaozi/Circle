package cn.muxiaozi.circle.game;

/**
 * Created by 慕宵子 on 2016/7/27.
 * <p>
 * 游戏的ID都记录在此
 */
public interface GameID {
    /**
     * 无效游戏ID
     */
    int NULL = 0;

    /**
     * 找我
     */
    int FIND_ME = 1;

    /**
     * 你画我猜
     */
    int DOODLE = 2;

    /**
     * FlappyBird
     */
    int FLAPPY_BIRD = 3;

    /**
     * 连连看
     */
    int LINK = 4;
}
