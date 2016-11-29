package cn.muxiaozi.circle.base;

/**
 * 我的圈圈配置
 *
 * @author 慕宵子
 */
public interface IConfig {
    String CONFIG_NAME = "cn.muxiaozi.circle.config";

    //管理员的ID
    String ADMIN_ID = "000000000000000";

    /**
     * 以键值对存储的用户个人信息
     */
    //是否第一次运行软件
    String OPT_FIRST_RUN = "OPT_FIRST_RUN";

    //用户唯一ID
    String OPT_UNIQUE_ID = "OPT_UNIQUE_ID";

    //姓名
    String OPT_NAME = "OPT_NAME";

    //头像
    String OPT_HEAD_PORTRAIT = "OPT_HEAD_PORTRAIT";

    //显示fps
    String OPT_FPS = "OPT_FPS";

    //背景音乐
    String OPT_MUSIC = "OPT_MUSIC";

    //音效
    String OPT_SOUNDS = "OPT_SOUNDS";

    /**
     * 连接参数配置
     */
    //默认端口号
    int LOCAL_PORT = 32334;

    //最大可连接客户端数
    int MAX_CLIENT_NUM = 8;

    //服务端的ip地址
    String KEY_REMOTE_IP = "KEY_REMOTE_IP";
    //游戏ID
    String KEY_GAME_ID = "KEY_GAME_ID";
    //玩家信息
    String KEY_USER_INFO = "KEY_USER_INFO";

    //到服务器的连接断开
    String ACTION_DISCONNECT = "cn.muxiaozi.disconnect";


    //Url连接
    interface Urls {
        //七牛云地址
        String QINIU_HOST = "http://qiniu.muxiaozi.cn/apk/circle/";
        String APK_EXT = ".apk";

        //服务器地址
        String HOST = "http://muxiaozi.cn";

        //获取版本信息
        String VERSION = HOST + "/app/circle/getVersion.php";
    }
}
