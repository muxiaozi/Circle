package cn.muxiaozi.circle.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by 慕宵子 on 2016/7/14.
 */
public final class AsyncRun {
    public static void run(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }
}
