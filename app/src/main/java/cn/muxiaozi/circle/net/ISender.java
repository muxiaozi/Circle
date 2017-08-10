package cn.muxiaozi.circle.net;

/**
 * Created by 慕宵子 on 2016/10/1 0001.
 *
 * 发送数据接口
 */
public interface ISender {
    /**
     * 发送数据包
     * @param data 数据包
     */
    void send(byte[] data);
}
