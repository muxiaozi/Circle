package cn.muxiaozi.circle.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * Created by 慕宵子 on 2016/7/11.
 * <p/>
 * 网络操作类
 */
public class NetWorkUtil {
    private static ConnectivityManager mConnectivityManager;
    private static WifiManager mWifiManager;

    /**
     * 初始化WIFI管理对象
     */
    public static void init(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static boolean isNetworkAvailable(Context context) {
        checkIsInit();
        NetworkInfo[] info = mConnectivityManager.getAllNetworkInfo();
        if (info != null) {
            for (NetworkInfo anInfo : info) {
                if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否成功连接到指定的ssid
     *
     * @param ssid 指定ssid
     */
    public static boolean isConnected(String ssid) {
        checkIsInit();
        if (mWifiManager.getConnectionInfo().getSSID().equals("\"" + ssid + "\"") ||
                mWifiManager.getConnectionInfo().getSSID().equals(ssid)) {
            NetworkInfo wifiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * wifi是否打开
     */
    public static boolean isWifiEnabled(Context context) {
        checkIsInit();
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mConnectivityManager.getActiveNetworkInfo() != null && mConnectivityManager
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

    /**
     * 判断当前网络是否是wifi网络
     *
     * @return boolean
     */
    public static boolean isWifi() {
        checkIsInit();
        NetworkInfo activeNetInfo = mConnectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断当前网络是否是3G网络
     *
     * @return boolean
     */
    public static boolean isMobile() {
        checkIsInit();
        NetworkInfo activeNetInfo = mConnectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    private static void checkIsInit() {
        if (mConnectivityManager == null || mWifiManager == null) {
            throw new RuntimeException("Please run method NetworkUtil.init(Context c) first!");
        }
    }

}
