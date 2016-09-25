package cn.muxiaozi.circle.game.link;

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

    public static final byte TYPE_TOUCH = 2;

    public static final byte TYPE_TURN = 3;

    public static final byte TYPE_SYNC_MAP = 4;

    /**
     * 开始游戏倒计时
     */
    public static byte[] packReady(int time) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_READY);
            dos.writeByte(time);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static int unpackReady(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            if (dis.readByte() == TYPE_READY) {
                return dis.readByte();
            }
        } catch (IOException ignored) {
        }
        return 3;
    }

    public static class TouchEntity {
        String imei;
        public int x;
        public int y;

        TouchEntity(String imei, int x, int y) {
            this.imei = imei;
            this.x = x;
            this.y = y;
        }
    }

    /**
     * 玩家触摸某一个方框
     */
    public static byte[] packTouch(TouchEntity entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_TOUCH);
            dos.writeUTF(entity.imei);
            dos.writeInt(entity.x);
            dos.writeInt(entity.y);
        } catch (IOException ignored) {
        }
        return baos.toByteArray();
    }

    public static TouchEntity unpackTouch(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            if (dis.readByte() == TYPE_TOUCH) {
                return new TouchEntity(dis.readUTF(), dis.readInt(), dis.readInt());
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * 轮流
     */
    public static byte[] packTurn(String imei) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_TURN);
            dos.writeUTF(imei);
        } catch (IOException ignored) {
        }
        return baos.toByteArray();
    }

    public static String unpackTurn(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            if (dis.readByte() == TYPE_TURN) {
                return dis.readUTF();
            }
        } catch (IOException ignored) {
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
            if (dis.readByte() == TYPE_SYNC_MAP) {
                byte[][] map = new byte[Res.COL_NUM][Res.ROW_NUM];
                for (int i = 0; i < Res.COL_NUM; i++) {
                    dis.read(map[i], 0, Res.ROW_NUM);
                }
                return map;
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}
