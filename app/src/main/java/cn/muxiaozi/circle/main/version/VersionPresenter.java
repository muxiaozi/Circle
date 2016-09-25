package cn.muxiaozi.circle.main.version;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.utils.CommonUtil;
import cn.muxiaozi.circle.utils.InfoUtil;
import cn.muxiaozi.circle.utils.LogUtil;
import cn.muxiaozi.circle.utils.NotificationUtil;

/**
 * Created by 慕宵子 on 2016/7/30.
 */
public class VersionPresenter extends VersionContract.Presenter {

    private VersionModule mVersionModule;

    private NotificationManager mNotificationManager;

    private Notification.Builder mBuilder;

    public VersionPresenter(Context context, VersionContract.View view) {
        super(context, view);
        mVersionModule = new VersionModule();
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(context);
    }

    @Override
    public void startUpgrade() {
        mVersionModule.downloadAPK(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case VersionModule.WHAT_UPDATE:
                        mBuilder.setSmallIcon(R.mipmap.nav_upgrade);
                        mBuilder.setContentTitle("正在下载...");
                        mBuilder.setContentText("");
                        mBuilder.setProgress(100, msg.arg1, false);
                        mNotificationManager.notify(0, mBuilder.build());
                        break;
                    case VersionModule.WHAT_SUCCESS:
                        mNotificationManager.cancel(0);
                        CommonUtil.install(mContext, (String) msg.obj);
                        break;
                    case VersionModule.WHAT_FAIL:
                        mNotificationManager.cancel(0);
                        mView.showTips("下载新版本失败!");
                        break;
                }
                return false;
            }
        }));
    }

    @Override
    public void checkUpgrade() {
        if (mVersionModule.isDownloading()) {
            mView.showTips("正在下载更新...");
        } else {
            mVersionModule.getNewestVersionInfo(new VersionModule.VersionInfoCallBack() {
                @Override
                public void onResult(VersionInfo info) {
                    if (info == null) {
                        mView.showTips("获取新版本失败！");
                    } else {
                        if (!InfoUtil.getVersion(mContext).equals(info.getVersion())) {
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
    }
}
