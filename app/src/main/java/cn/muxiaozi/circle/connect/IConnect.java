package cn.muxiaozi.circle.connect;

import java.net.InetAddress;
import java.util.List;

/**
 * Created by 慕宵子 on 2016/11/29 0029.
 *
 * 连接接口
 */
public interface IConnect {
    /**
     * 一般回调接口
     */
    interface Callback{
        void onSuccess();

        void onFailure(String errMsg);
    }

    /**
     * 搜索回调接口
     */
    interface OnResultListener{
        /**
         * 返回搜索结果集
         */
        void onResult(List<AccessPoint> aps);
    }

    /**
     * 创建圈圈
     * @param callback 创建结果回调接口
     */
    void invite(Callback callback);

    /**
     * 搜索圈圈
     * @param resultListener 搜索结果回调接口
     */
    void search(OnResultListener resultListener);

    /**
     * 加入圈圈
     * @param ap 需要加入的连接点
     * @param callback 加入结果回调接口
     */
    void join(AccessPoint ap, Callback callback);

    /**
     * 获取主机地址
     * @return 如果是主机则返回地址，否则返回null
     */
    InetAddress getRemoteAddress();
}
