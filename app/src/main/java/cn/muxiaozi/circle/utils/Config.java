package cn.muxiaozi.circle.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import cn.muxiaozi.circle.base.IConfig;
import cn.muxiaozi.circle.room.UserBean;

/**
 * Created by 慕宵子 on 2016/7/17.
 * <p/>
 * 软件配置
 */
public class Config implements IConfig{

    /**
     * 配置管理者
     */
    private static SharedPreferences mPreferences;

    /**
     * 初始化配置类
     */
    public static void init(Context context){
        if(mPreferences == null){
            mPreferences = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
        }
    }

    /**
     * 获取手机识别号
     *
     * @return 如果存在则返回正确识别号，否则返回null
     */
    public static String getUniqueID() {
        return mPreferences.getString(IConfig.OPT_UNIQUE_ID, null);
    }

    /**
     * 获取昵称
     *
     * @return 昵称
     */
    public static String getName() {
        return mPreferences.getString(OPT_NAME, "Circle");
    }

    /**
     * 获取头像
     *
     * @return 头像id
     */
    public static int getHeadPortrait() {
        return mPreferences.getInt(OPT_HEAD_PORTRAIT, 0);
    }

    /**
     * 获取自己的信息
     *
     * @return 自己的信息
     */
    public static UserBean getMyInfo() {
        UserBean myInfo = new UserBean();
        myInfo.setUniqueID(getUniqueID());
        myInfo.setHeadPortrait(getHeadPortrait());
        myInfo.setName(getName());
        return myInfo;
    }

    /**
     * 是否第一次运行
     * @return
     */
    public static boolean isFirstRun(){
        return mPreferences.getBoolean(OPT_FIRST_RUN, true);
    }

    /**
     * 获取软件版本
     *
     * @return 软件版本
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "获取版本失败!";
        }
    }
}
