package cn.muxiaozi.circle.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.room.UserBean;

/**
 * Created by 慕宵子 on 2016/7/17.
 * <p/>
 * 记录了用户的个人信息
 */
public class InfoUtil {
    public static final String DEFAULT_IMEI = "000000000000000";

    public static final String IMEI = "IMEI";               //IMEI手机识别号
    public static final String NAME = "NAME";               //姓名
    public static final String HEAD_IMAGE = "HEAD_IMAGE";   //头像
    public static final String AUTOGRAPH = "AUTOGRAPH";     //个性签名

    /**
     * 获取手机识别号
     *
     * @return 如果可以获取，则返回正确识别号，否则返回默认识别号
     */
    public static String getImei(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(Constants.CIRCLE_CONFIG, Context.MODE_PRIVATE);
        String imei = preferences.getString(IMEI, DEFAULT_IMEI);
        if (imei.equals(DEFAULT_IMEI)) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                preferences.edit()
                        .putString(IMEI, imei)
                        .apply();
            } else {
                preferences.edit()
                        .putBoolean(Constants.ConfigOption.FIRST_RUN, true)
                        .apply();
                ToastUtil.showShort(context, "遇到点小麻烦，请重新打开应用！");
                System.exit(0);
            }
        }
        return imei;


    }

    /**
     * 获取昵称
     *
     * @return 昵称
     */
    public static String getName(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(Constants.CIRCLE_CONFIG, Context.MODE_PRIVATE);
        return preferences.getString(NAME, "Circle");
    }

    /**
     * 获取个新签名
     *
     * @return 个性签名
     */
    public static String getAutograph(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(Constants.CIRCLE_CONFIG, Context.MODE_PRIVATE);
        return preferences.getString(AUTOGRAPH, "我的圈圈我做主！");
    }

    /**
     * 获取头像
     *
     * @return 头像id
     */
    public static int getHeadImage(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(Constants.CIRCLE_CONFIG, Context.MODE_PRIVATE);
        return preferences.getInt(HEAD_IMAGE, 0);
    }

    /**
     * 获取自己的信息
     *
     * @return 自己的信息
     */
    public static UserBean getMyInfo(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(Constants.CIRCLE_CONFIG, Context.MODE_PRIVATE);
        UserBean myInfo = new UserBean();
        myInfo.setImei(getImei(context));
        myInfo.setHeadImage(preferences.getInt(HEAD_IMAGE, 0));
        myInfo.setName(preferences.getString(NAME, "Circle"));
        myInfo.setAutograph(preferences.getString(AUTOGRAPH, "我的圈圈我做主！"));
        myInfo.setPrepare(false);
        return myInfo;
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
