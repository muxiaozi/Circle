package cn.muxiaozi.circle.libgdx.link;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by 慕宵子 on 2016/9/24 0024.
 */

public abstract class DataFactory {
    public static final byte TYPE_READY = 1;

    public static final byte TYPE_CLEAR_DIAMONDS = 2;

    public static final byte TYPE_TURN = 3;

    public static final byte TYPE_SYNC_MAP = 5;

    /**
     * 开始游戏倒计时
     */
    public static byte[] packReady(int time) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_READY);
            dos.writeByte((byte) time);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static int unpackReady(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            dis.readByte();
            return dis.readByte();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 3;
    }

    /**
     * 轮流
     */
    public static byte[] packTurn(int index) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_TURN);
            dos.writeInt(index);
        } catch (IOException ignored) {
        }
        return baos.toByteArray();
    }

    public static int unpackTurn(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            dis.readByte();
            return dis.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static class DoubleKillEntity {
        public int index;
        public int x1;
        public int y1;
        public int x2;
        public int y2;

        public DoubleKillEntity(int index, int x1, int y1, int x2, int y2) {
            this.index = index;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    /**
     * 消除两个方块
     * <p>
     * rect.left    //第一个方块的x坐标
     * rect.top     //第一个方块的y坐标
     * rect.right   //第二个方块的x坐标
     * rect.bottom  //第二个方块的y坐标
     */
    public static byte[] packClearDiamonds(DoubleKillEntity entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_CLEAR_DIAMONDS);
            dos.writeInt(entity.index);  //索引
            dos.writeInt(entity.x1);    //第一个方块的x坐标
            dos.writeInt(entity.y1);     //第一个方块的y坐标
            dos.writeInt(entity.x2);   //第二个方块的x坐标
            dos.writeInt(entity.y2);  //第二个方块的y坐标

        } catch (IOException ignored) {
        }
        return baos.toByteArray();
    }

    public static DoubleKillEntity unpackClearDiamonds(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            dis.readByte();
            return new DoubleKillEntity(dis.readInt(), dis.readInt(), dis.readInt(), dis.readInt(), dis.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 同步地图
     */
    public static byte[] packSyncMap(byte[][] map) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_SYNC_MAP);
            for (int i = 0; i < Res.COL_NUM; i++) {
                dos.write(map[i], 0, Res.ROW_NUM);
            }
        } catch (IOException ignored) {
        }
        return baos.toByteArray();
    }

    public static byte[][] unpackSyncMap(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            dis.readByte();
            byte[][] map = new byte[Res.COL_NUM][Res.ROW_NUM];
            for (int i = 0; i < Res.COL_NUM; i++) {
                dis.read(map[i], 0, Res.ROW_NUM);
            }
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
