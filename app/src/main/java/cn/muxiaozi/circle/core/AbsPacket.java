package cn.muxiaozi.circle.core;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by 慕宵子 on 2017/1/17 0017.
 * <p>
 * 抽象数据包
 */
public abstract class AbsPacket {
    private static final String TAG = "AbsPacket";

    //打包帮助对象
    private static final ByteArrayOutputStream mBaos = new ByteArrayOutputStream(512);
    private static final DataOutputStream mDos = new DataOutputStream(mBaos);

    //消息包类型
    public byte mType;
    //是否需要转发
    public boolean mNeedTransmit = false;

    /**
     * 打包
     *
     * @param dos 输出流
     * @throws IOException 打包失败
     */
    protected abstract void pack(DataOutputStream dos) throws IOException;

    /**
     * 解包
     *
     * @param dis 输入流
     * @throws IOException 解包失败
     */
    protected abstract void unpack(DataInputStream dis) throws IOException;

    /**
     * 处理包
     *
     * @param context 上下文
     */
    public abstract void handle(Context context);

    /**
     * 把包内容转换成Byte数组
     *
     * @return byte数组
     */
    public byte[] toByteArray() {
        mBaos.reset();
        try {
            //打包类型/是否转发
            mDos.writeByte(mType);
            mDos.writeBoolean(mNeedTransmit);
            pack(mDos);
        } catch (IOException e) {
            Log.e(TAG, "toByteArray: 转换byte数组错误");
            return null;
        }
        return mBaos.toByteArray();
    }

    /**
     * 转换成对象
     *
     * @param data 数据包
     * @throws IOException 返回
     */
    public void toObj(byte[] data) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        mType = dis.readByte();
        mNeedTransmit = dis.readBoolean();
        unpack(dis);
    }

    /**
     * 获取包类型
     *
     * @param data 数据包
     * @return 数据包类型
     */
    public static byte getType(byte[] data) {
        return data[0];
    }

    /**
     * 是否需要转发
     *
     * @param data 数据包
     * @return 如果需要转发，返回true，否则返回false
     */
    public static boolean needTransmit(byte[] data) {
        return data[1] != 0;
    }
}