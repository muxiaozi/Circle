package cn.muxiaozi.circle.connect.wifiap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import cn.muxiaozi.circle.utils.AsyncRun;
import cn.muxiaozi.circle.utils.Config;

/**
 * Created by 慕宵子 on 2016/7/14.
 * <p>
 * wifi操作模块
 */
class WifiModel {
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;
    private TelephonyManager mTelephonyManager;

    private String mMySSID;  //创建wifi热点时所用的SSID

    interface OnProcessCallBack {
        void onSuccess(String message);

        void onFailure(Exception e);
    }

    WifiModel(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        mMySSID = "shw" + Config.getName(context);
    }

    /**
     * 打开wifi
     */
    void openWifi(final OnProcessCallBack callBack) {
        //检测wifi热点如果是开启状态就关闭
        if (isWifiApEnabled()) {
            closeWifiAP();
        }
        mWifiManager.setWifiEnabled(true);

        if (callBack == null) {
            return;
        }

        //监控wifi打开状态,倒计时10秒
        new Thread(new Runnable() {
            int deadline = 100;

            @Override
            public void run() {
                try {
                    while (--deadline > 0) {
                        Thread.sleep(100);
                        if (mWifiManager.isWifiEnabled()) {
                            AsyncRun.run(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onSuccess("wifi打开成功！");
                                }
                            });
                            return;
                        }
                    }
                } catch (final InterruptedException e) {
                    //处理异常错误情况
                    AsyncRun.run(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailure(e);
                        }
                    });
                }

                //处理超时情况
                AsyncRun.run(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(new Exception("Wifi打开失败！"));
                    }
                });
            }
        }).start();
    }

    /**
     * 关闭wifi
     */
    private void closeWifi() {
        mWifiManager.setWifiEnabled(false);
    }

    /**
     * 打开wifi热点
     */
    void openWifiAP(final OnProcessCallBack callBack) {
        openWifiAP(mMySSID, "");

        if (callBack == null) {
            return;
        }

        //监控wifi热点打开状态,倒计时10秒
        new Thread(new Runnable() {
            int deadline = 100;

            @Override
            public void run() {
                try {
                    while (--deadline > 0) {
                        Thread.sleep(100);
                        if (isWifiApEnabled()) {
                            AsyncRun.run(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onSuccess("wifi热点打开成功！");
                                }
                            });
                            return;
                        }
                    }
                } catch (final InterruptedException e) {
                    //处理异常错误情况
                    AsyncRun.run(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailure(e);
                        }
                    });
                }

                //处理超时情况
                AsyncRun.run(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(new Exception("Wifi热点打开失败！"));
                    }
                });
            }
        }).start();
    }

    /**
     * 关闭wifi热点
     */
    void closeWifiAP() {
        if (isWifiApEnabled()) {
            try {
                Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);

                method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method.invoke(mWifiManager, config, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始搜索附近wifi
     */
    List<ScanResult> getScanResults() {
        mWifiManager.startScan();
        return mWifiManager.getScanResults();
    }

    /**
     * 得到接入点的SSID
     *
     * @return 接入点的SSID
     */
    String getSSID() {
        return mWifiManager.getConnectionInfo().getSSID();
    }

    /**
     * 得到本地的IP地址
     *
     * @return 本地的IP地址
     */
    public String getLocalIPAddress() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return intToIp(ipAddress);
    }

    /**
     * 得到主机的Ip地址
     *
     * @return 主机的IP地址
     */
    String getRemoteIpAddress() {
        DhcpInfo info = mWifiManager.getDhcpInfo();
        int ipAddress = info.serverAddress;
        return intToIp(ipAddress);
    }

    /**
     * 把int转化为IP地址
     *
     * @param i int型ip地址
     * @return ip地址
     */
    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    /**
     * 得到连接的ID
     *
     * @return 连接的ID
     */
    int getNetworkId() {
        return mWifiManager.getConnectionInfo().getNetworkId();
    }

    /**
     * 断开指定ID的网络
     *
     * @param netId 指定ID
     */
    void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.removeNetwork(netId);
    }

    /**
     * 构建将要连接的wifi信息
     *
     * @param SSID     ssid
     * @param Password 密码
     * @param Type     类型
     * @return WIFI的ID号码
     */
    private int CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = findExistsWifiConfiguration("\"" + SSID + "\"");

        if (config == null) {
            config = new WifiConfiguration();
            config.allowedAuthAlgorithms.clear();
            config.allowedGroupCiphers.clear();
            config.allowedKeyManagement.clear();
            config.allowedPairwiseCiphers.clear();
            config.allowedProtocols.clear();
            config.SSID = "\"" + SSID + "\"";

            if (Type == 1) //WIFICIPHER_NOPASS
            {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }
            if (Type == 2) // WIFICIPHER_WEP
            {
                if (Password.length() != 0) {
                    int length = Password.length();
                    // WEP-40, WEP-104, and 256-bit WEP
                    // (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58) && Password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = Password;
                    } else {
                        config.wepKeys[0] = '"' + Password + '"';
                    }
                }
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            }
            if (Type == 3) //WIFICIPHER_WPA
            {
                config.preSharedKey = "\"" + Password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
            }
            return mWifiManager.addNetwork(config);
        }
        return config.networkId;
    }

    /**
     * 查找是否已经连接过指定的SSID
     *
     * @param SSID ssid
     * @return 如果是，则返回连接信息，否则返回null
     */
    private WifiConfiguration findExistsWifiConfiguration(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals(SSID)) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 开始连接到指定wifi热点
     */
    void connect(final String ssid, final OnProcessCallBack callBack) {
        //如果wifi不可用，则返回错误
        if (!isWifiEnabled()) {
            AsyncRun.run(new Runnable() {
                @Override
                public void run() {
                    callBack.onFailure(new Exception("请先打开wifi！"));
                }
            });
            return;
        }

        int wcgID = CreateWifiInfo(ssid, "", 1);
        mWifiManager.enableNetwork(wcgID, true);

        //监控wifi是否成功连接,倒计时10秒
        new Thread(new Runnable() {
            int deadline = 100;

            @Override
            public void run() {
                try {
                    while (--deadline > 0) {
                        Thread.sleep(100);
                        if (isConnected(ssid)) {
                            AsyncRun.run(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onSuccess("已成功连接到【" + ssid.substring(3) + "】");
                                }
                            });
                            return;
                        }
                    }
                } catch (final InterruptedException e) {
                    //处理异常错误情况
                    AsyncRun.run(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailure(e);
                        }
                    });
                }

                //处理超时情况
                AsyncRun.run(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(new Exception("连接到【" + ssid.substring(3) + "】失败！"));
                    }
                });
            }
        }).start();
    }

    /**
     * 打开wifi热点
     *
     * @param ssid   热点名称
     * @param passwd 热点密码
     */
    private void openWifiAP(String ssid, String passwd) {
        //检测wifi如果开启就先关闭
        if (isWifiEnabled()) {
            closeWifi();
        }

        Method method;

        try {
            method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();

            netConfig.SSID = ssid;
            netConfig.preSharedKey = passwd;

            if (passwd.isEmpty()) {
                netConfig.SSID = ssid;
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                netConfig.preSharedKey = null;
            } else {
                netConfig.SSID = ssid;
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                netConfig.preSharedKey = passwd;
            }

            method.invoke(mWifiManager, netConfig, true);

        } catch (IllegalArgumentException | IllegalAccessException | SecurityException |
                InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    private boolean isConnected(String ssid) {
        if (mWifiManager.getConnectionInfo().getSSID().equals("\"" + ssid + "\"") ||
                mWifiManager.getConnectionInfo().getSSID().equals(ssid)) {
            NetworkInfo wifiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * wifi热点是否已经打开
     *
     * @return wifi热点状态
     */
    private boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean isMobileDataOpen() {
        return mTelephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED;
    }

    void setMobileData(boolean enabled) {
        try {
            Method setMobileDataEnable = mConnectivityManager.getClass()
                    .getDeclaredMethod("setMobileDataEnabled", boolean.class);
            setMobileDataEnable.invoke(mConnectivityManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
