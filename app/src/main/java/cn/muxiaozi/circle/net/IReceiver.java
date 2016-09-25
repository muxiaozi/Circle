package cn.muxiaozi.circle.net;

/**
 * Created by 慕宵子 on 2016/7/18.
 *
 * 数据接收接口
 */
public interface IReceiver {
    /**
     * 接受数据
     * @param data 数据
     * @return 是否拦截数据，阻止其传递到其他客户端（只对服务端有效）
     */
    boolean receive(byte[] data);
}
