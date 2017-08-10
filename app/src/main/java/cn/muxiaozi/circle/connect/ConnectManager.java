package cn.muxiaozi.circle.connect;

import android.content.Context;

import cn.muxiaozi.circle.connect.bluetooth.Bluetooth;
import cn.muxiaozi.circle.connect.lan.Lan;
import cn.muxiaozi.circle.connect.wifiap.WifiAP;
import cn.muxiaozi.circle.connect.wifip2p.WifiP2P;

/**
 * Created by 慕宵子 on 2016/11/30 0030.
 *
 * 利用工厂模式返回连接对象
 */
public class ConnectManager {

    /**
     * 连接方式
     */
    //wifi热点连接
    public static final int WIFI_AP = 1;

    //wifi direct连接
    public static final int WIFI_P2P = 2;

    //蓝牙连接
    public static final int BLUETOOTH = 3;

    //局域网
    public static final int LAN = 4;

    /**
     * 根据不同的连接方式返回不同的连接对象
     * @param mode 连接方式
     * @return 连接对象
     */
    public static IConnect connect(Context context, int mode){
        switch (mode){
            case WIFI_AP:
                return new WifiAP(context);

            case WIFI_P2P:
                return new WifiP2P(context);

            case BLUETOOTH:
                return new Bluetooth(context);

            case LAN:
                return new Lan();

            default:
                return new WifiP2P(context);
        }
    }
}
