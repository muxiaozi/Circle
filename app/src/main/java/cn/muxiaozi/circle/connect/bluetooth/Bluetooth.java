package cn.muxiaozi.circle.connect.bluetooth;

import android.content.Context;

import java.net.InetAddress;

import cn.muxiaozi.circle.connect.AccessPoint;
import cn.muxiaozi.circle.connect.IConnect;

/**
 * Created by 慕宵子 on 2016/11/30 0030.
 *
 * 通过蓝牙的方式连接
 */
public class Bluetooth implements IConnect{

    public Bluetooth(Context context) {
        
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
