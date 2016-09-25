package cn.muxiaozi.circle.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import cn.muxiaozi.circle.R;

/**
 * Created by 慕宵子 on 2016/5/18.
 * <p/>
 * 通知管理类
 */
public class NotificationUtil {

    //普通通知
    private static final int ID_COMMON = 1;

    //下载通知
    private static final int ID_PROGRESS = 2;

    //通知管理对象
    private static NotificationManager mNotificationManager;

    private static Notification.Builder builder;

    public static void notifyMessage(Context context, String ticker, String title, String message) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(
                        BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(message);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(ID_COMMON, notification);
    }

    public static void notifyProgress(Context context, int progress) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new Notification.Builder(context);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(
                            BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setTicker("开始更新")
                    .setProgress(100, 0, false)
                    .setContentTitle("正在下载最新版圈圈");
        }

        builder.setProgress(100, progress, false);

        Notification notification = builder.build();

        mNotificationManager.notify(ID_PROGRESS, notification);
    }
}
