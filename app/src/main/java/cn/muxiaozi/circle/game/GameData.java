package cn.muxiaozi.circle.game;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by 慕宵子 on 2016/9/26 0026.
 *
 * 负责分发游戏数据
 * 如果把是否拦截的操作放在游戏中，势必会影响消息的接受，所以在这里快速返回是否拦截，
 * 并且能够把消息送达每个游戏中
 */

public class GameData {
    private ArrayBlockingQueue<byte[]> dataQueue;


}
