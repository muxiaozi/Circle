package cn.muxiaozi.circle.game.flappy_bird;

/**
 * 游戏状态
 * 
 * @xietansheng
 */
public enum BirdState {

    /** 准备状态 */
    ready,

    /** 小鸟处于飞翔状态 */
    fly,

    /** 小鸟处于掉落状态 */
    drop,

    /** 小鸟撞倒管道或者地板上 */
    die,

    /** 游戏结束,显示游戏结果 */
    gameOver
}
