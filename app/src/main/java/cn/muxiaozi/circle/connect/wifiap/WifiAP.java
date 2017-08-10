package cn.muxiaozi.circle.connect.wifiap;

import android.content.Context;

import java.net.InetAddress;

import cn.muxiaozi.circle.connect.AccessPoint;
import cn.muxiaozi.circle.connect.IConnect;

/**
 * Created by 慕宵子 on 2016/11/29 0029.
 *
 * 通过WIFI热点相连
 */

public class WifiAP implements IConnect {

    public WifiAP(Context context) {
    }

    @Override
    public void invite(Callback callback) {

    }

    @Override
    public void search(OnResultListener resultListener) {

    }

    @Override
    public void join(AccessPoint ap, Callback callback) {

    }

    @Override
    public InetAddress getRemoteAddress() {
        return null;
    }

}
