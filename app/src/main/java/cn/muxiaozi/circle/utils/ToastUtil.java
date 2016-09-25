package cn.muxiaozi.circle.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 慕宵子 on 2016/7/19.
 * <p>
 * 提示框工具
 */
public class ToastUtil {

    private static Toast mToast;

    public static void showShort(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void showLong(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }
}
