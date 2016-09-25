package cn.muxiaozi.circle.base;

/**
 * 常量配置类
 *
 * @author 慕宵子
 */
public class Constants {

    //默认端口号
    public static final int LOCAL_PORT = 32334;

    //最大可连接客户端数
    public static final int MAX_CLIENT_NUM = 8;

    //服务端的ip地址
    public static final String KEY_REMOTE_IP = "KEY_REMOTE_IP";
    //游戏ID
    public static final String KEY_GAME_ID = "KEY_GAME_ID";
    //玩家信息
    public static final String KEY_USER_INFO = "KEY_USER_INFO";

    //圈圈配置信息
    public static final String CIRCLE_CONFIG = "CircleConfig";

    public interface ConfigOption{
        //是否第一次运行软件
        String FIRST_RUN = "firstRun";

        //显示fps
        String FPS = "showFps";
        //背景音乐
        String MUSIC = "bg_music";
        //音效
        String SOUNDS = "sounds";
    }

    //到服务器的连接断开
    public static final String ACTION_DISCONNECT = "cn.muxiaozi.disconnect";

}
