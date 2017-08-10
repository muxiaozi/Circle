package cn.muxiaozi.circle.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Build;

import java.util.List;

import cn.muxiaozi.circle.core.BasePresenter;
import cn.muxiaozi.circle.core.IConfig;
import cn.muxiaozi.circle.connect.WifiModule;
import cn.muxiaozi.circle.core.CoreService;
import cn.muxiaozi.circle.room.RoomActivity;
import cn.muxiaozi.circle.utils.ToastUtil;

/**
 * Created by 慕宵子 on 2016/4/22.
 */
class MainPresenter extends BasePresenter<MainView> {
    private static final int STATE_NONE = 0;
    private static final int STATE_WIFI = 1;
    private static final int STATE_WIFI_AP = 2;

    private int mCurrentState = STATE_NONE;

    private boolean isWifiAlreadyOpen;
    private boolean isDataAlreadyOpen;

    MainPresenter(Context context, MainView view) {
        super(context, view);

        mWifiModule = new WifiModule(context);

        isWifiAlreadyOpen = mWifiModule.isWifiEnabled();
        isDataAlreadyOpen = mWifiModule.isMobileDataOpen();

        //关闭wifi热点
        mWifiModule.closeWifiAP();

        //启动消息服务
        Intent intent = new Intent(mContext, CoreService.class);
        intent.setFlags(CoreService.STATE_NONE);
        mContext.startService(intent);

        //注册广播接受
        mContext.registerReceiver(mReceiver, new IntentFilter(IConfig.ACTION_DISCONNECT));
    }

    public void startJoin() {
        mView.showProgressDialog("准备搜索圈圈...");
        mWifiModule.openWifi(new WifiModule.OnProcessCallBack() {
            @Override
            public void onSuccess(String message) {
                mView.hideProgressDialog();

                //安卓6.0以上搜索热点需要打开GPS
                LocationManager locationManager = (LocationManager)
                        mContext.getSystemService(Context.LOCATION_SERVICE);

                // 判断GPS模块是否开启，如果没有则开启
                if (Build.VERSION.SDK_INT > 22 && !locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                    mView.showOpenGpsDialog();
                    ToastUtil.showLong(mContext, "请打开GPS搜索附近圈圈！");
                } else {
                    mView.showNearby();
                }
            }

            @Override
            public void onFailure(Exception e) {
                mView.hideProgressDialog();
                mView.showTips(e.getMessage());
            }
        });
    }

    public void startConnect(String ssid) {
        mView.showProgressDialog("正在加入圈圈...");
        mWifiModule.connect(ssid, new WifiModule.OnProcessCallBack() {
            @Override
            public void onSuccess(String message) {
                mView.hideProgressDialog();
                mView.showTips(message);
                mView.setFabMenuWorkState(true);
                mCurrentState = STATE_WIFI;

                //启动客户端模式
                Intent intent = new Intent(mContext, CoreService.class);
                intent.setFlags(CoreService.STATE_CLIENT);
                intent.putExtra(IConfig.KEY_REMOTE_IP, mWifiModule.getRemoteIpAddress());
                mContext.startService(intent);

                mContext.startActivity(new Intent(mContext, RoomActivity.class));
            }

            @Override
            public void onFailure(Exception e) {
                mView.hideProgressDialog();
                mView.showTips(e.getMessage());
            }
        });
    }

    public void startInvite() {
        mView.showProgressDialog("正在创建圈圈...");
        if (mWifiModule.isMobileDataOpen()) {
            mWifiModule.setMobileData(false);
            mView.showTips("为了保护您的流量，暂时将数据连接关闭！");
        }
        mWifiModule.openWifiAP(new WifiModule.OnProcessCallBack() {
            @Override
            public void onSuccess(String message) {
                mView.hideProgressDialog();
                mView.showTips(message);
                mCurrentState = STATE_WIFI_AP;
                mView.setFabMenuWorkState(true);

                //启动服务器模式
                Intent intent = new Intent(mContext, CoreService.class);
                intent.setFlags(CoreService.STATE_SERVER);
                mContext.startService(intent);
            }

            @Override
            public void onFailure(Exception e) {
                mView.hideProgressDialog();
                mView.showTips(e.getMessage());
            }
        });
    }

    public List<ScanResult> getNearby() {
        return mWifiModule.getScanResults();
    }

    /**
     * 广播接受器
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case IConfig.ACTION_DISCONNECT:
                    mView.setFabMenuWorkState(false);
                    break;
            }
        }
    };

    public void cancel() {
        if (mCurrentState == STATE_WIFI) {
            if (mWifiModule.getSSID().contains("shw")) {
                mWifiModule.disconnectWifi(mWifiModule.getNetworkId());
            }
        } else if (mCurrentState == STATE_WIFI_AP) {
            mWifiModule.closeWifiAP();
        }
        mCurrentState = STATE_NONE;

        //启动消息服务
        Intent intent = new Intent(mContext, CoreService.class);
        intent.setFlags(CoreService.STATE_NONE);
        mContext.startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isWifiAlreadyOpen) {
            mWifiModule.openWifi(null);
        }

        if (isDataAlreadyOpen) {
            mWifiModule.setMobileData(true);
        }

        //关闭消息服务
        Intent intent = new Intent(mContext, CoreService.class);
        mContext.stopService(intent);

        //解除广播
        mContext.unregisterReceiver(mReceiver);
    }
}
