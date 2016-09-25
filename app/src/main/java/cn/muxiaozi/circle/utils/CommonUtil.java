package cn.muxiaozi.circle.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by 慕宵子 on 2016/6/4.
 *
 * 公用工具类
 */
public class CommonUtil {

    /**
     * 安装apk
     * @param context 上下文
     * @param url 安装包路径
     */
    public static void install(Context context, String url) {
        if(context == null)
            return;
        File apkFile = new File(url);
        if(apkFile.exists()){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }
}
