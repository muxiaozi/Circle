package cn.muxiaozi.circle.version;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import java.io.File;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.core.BasePresenter;
import cn.muxiaozi.circle.utils.Config;

/**
 * Created by 慕宵子 on 2016/7/30.
 * <p>
 * 版本控制
 */
public class VersionPresenter extends BasePresenter<VersionView> {

    private VersionModel mVersionModel;
    private NotificationManager mNotificationManager;
    private Notification.Builder mBuilder;

    private static final int NOTIFY_ID = 2;

    public VersionPresenter(Context context, VersionView view) {
        super(context, view);
        mVersionModel = new VersionModel();
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(context)
                .setContentTitle("正在下载圈圈...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
    }

    public void startUpgrade() {
        mVersionModel.downloadAPK(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case VersionModel.WHAT_UPDATE:
                        mBuilder.setProgress(100, msg.arg1, false);
                        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
                        break;
                    case VersionModel.WHAT_SUCCESS:
                        mBuilder.setContentTitle("下载成功,点击安装！");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File((String) msg.obj)),
                                "application/vnd.android.package-archive");
                        mBuilder.setContentIntent(PendingIntent.getActivity(
                                mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
                        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
                        break;
                    case VersionModel.WHAT_FAIL:
                        mBuilder.setContentTitle("下载新版本失败！");
                        mBuilder.setContentIntent(PendingIntent.getActivity(
                                mContext, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
                        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
                        mView.showTips("下载新版本失败!");
                        break;
                }
                return false;
            }
        }));
    }

    public void checkUpgrade() {
        if (mVersionModel.isDownloading()) {
            mView.showTips("正在下载更新...");
        } else {
            mVersionModel.getNewestVersionInfo(new VersionModel.VersionInfoCallBack() {
                @Override
                public void onResult(VersionInfo info) {
                    if (info == null) {
                        mView.showTips("获取新版本失败！");
                    } else {
                        if (!Config.getVersion(mContext).equals(info.getVersion())) {
                            mView.showVersionDialog(true, info);
                        } else {
                            mView.showVersionDialog(false, null);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
