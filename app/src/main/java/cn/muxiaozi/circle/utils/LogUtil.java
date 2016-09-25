package cn.muxiaozi.circle.utils;

import android.util.Log;

/**
 * Created by 慕宵子 on 2016/7/11.
 */
public class LogUtil {

    private static final String TAG = "Circle";
    private static boolean isDebug = true;

    public static void setDebug(boolean isDebug){
        LogUtil.isDebug = isDebug;
    }

    public static void i(String msg){
        if(isDebug)
            Log.i(TAG, msg);
    }

    public static void v(String msg){
        if(isDebug)
            Log.v(TAG, msg);
    }

    public static void d(String msg){
        if(isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg){
        if(isDebug)
            Log.e(TAG, msg);
    }

    public static void w(String msg){
        if(isDebug)
            Log.w(TAG, msg);
    }
}
