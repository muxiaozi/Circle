package cn.muxiaozi.circle.connect.wifip2p;

import android.content.Context;

import java.net.InetAddress;

import cn.muxiaozi.circle.connect.AccessPoint;
import cn.muxiaozi.circle.connect.IConnect;

/**
 * Created by 慕宵子 on 2016/11/30 0030.
 *
 * 通过WIFI直连相连
 */

public class WifiP2P implements IConnect {

    public WifiP2P(Context context) {
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
