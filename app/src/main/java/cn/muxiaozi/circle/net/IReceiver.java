package cn.muxiaozi.circle.net;

/**
 * Created by 慕宵子 on 2016/7/18.
 *
 * 实现这个接口将具备接受信息的能力
 */
public interface IReceiver {
    /**
     * 接受数据
     * @param data 数据
     */
    void receive(byte[] data);
}
