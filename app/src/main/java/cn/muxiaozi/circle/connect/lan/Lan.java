package cn.muxiaozi.circle.connect.lan;

import java.net.InetAddress;

import cn.muxiaozi.circle.connect.AccessPoint;
import cn.muxiaozi.circle.connect.IConnect;

/**
 * Created by 慕宵子 on 2017/1/15 0015.
 *
 * 通过局域网的方式连接
 */
public class Lan implements IConnect {
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
