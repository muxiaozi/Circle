package cn.muxiaozi.circle.net;

/**
 * Created by lol_w on 2016/9/1 0001.
 */
interface ISocket {

    /**
     * 发送数据
     * @param data 数据
     */
    void send(byte[] data);

    /**
     * 关闭Socket
     */
    void close();
}
