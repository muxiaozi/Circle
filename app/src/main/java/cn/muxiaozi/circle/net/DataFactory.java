package cn.muxiaozi.circle.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cn.muxiaozi.circle.room.UserBean;

/**
 * Created by 慕宵子 on 2016/7/19.
 * <p>
 * 数据工厂
 * <p>
 * 所有的系统数据类型均为负数
 */
public abstract class DataFactory {

    //心跳数据
    static final byte TYPE_HEART_BEAT = 0;

    //朋友加入
    public static final byte TYPE_FRIEND_IN = -1;

    //朋友离开
    public static final byte TYPE_FRIEND_OUT = -2;

    //与服务器连接断开
    public static final byte TYPE_DISCONNECT_SERVER = -3;

    //房间内准备
    public static final byte TYPE_PREPARE = -4;

    //开始游戏（丛游戏大厅到游戏界面）
    public static final byte TYPE_START_GAME = -5;

    /**
     * 打包心跳数据
     *
     * @return 数据
     */
    public static byte[] packHeartBeat() {
        byte[] data = new byte[1];
        data[0] = TYPE_HEART_BEAT;
        return data;
    }

    /**
     * 打包服务器关闭消息
     *
     * @return 数据
     */
    public static byte[] packDisconnectServer() {
        byte[] data = new byte[1];
        data[0] = TYPE_DISCONNECT_SERVER;
        return data;
    }

    /**
     * 打包朋友加入信息
     *
     * @param userBean 朋友信息
     * @return 数据
     */
    public static byte[] packFriendIn(UserBean userBean) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_FRIEND_IN);
            dos.writeInt(userBean.getHeadImage());
            dos.writeUTF(userBean.getName());
            dos.writeUTF(userBean.getAutograph());
            dos.writeUTF(userBean.getImei());
            dos.writeBoolean(userBean.isPrepare());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    /**
     * 解包朋友加入数据
     *
     * @param data 数据
     * @return 朋友信息
     */
    public static UserBean unpackFriendIn(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        UserBean userBean = null;
        try {
            if (dis.readByte() == TYPE_FRIEND_IN) {
                userBean = new UserBean();
                userBean.setHeadImage(dis.readInt());
                userBean.setName(dis.readUTF());
                userBean.setAutograph(dis.readUTF());
                userBean.setImei(dis.readUTF());
                userBean.setPrepare(dis.readBoolean());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userBean;
    }

    /**
     * 打包朋友退出信息
     *
     * @param imei 朋友识别号
     * @return 数据
     */
    public static byte[] packFriendOut(String imei) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_FRIEND_OUT);
            dos.writeUTF(imei);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    /**
     * 解包朋友退出消息
     *
     * @param data 数据
     * @return 朋友识别号
     */
    public static String unpackFriendOut(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            if (dis.readByte() == TYPE_FRIEND_OUT) {
                return dis.readUTF();
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * 准备状态
     */
    public static class StartGameEntity {
        public int gameID;
        public String[] players;

        public StartGameEntity(int gameID, String[] players) {
            this.gameID = gameID;
            this.players = players;
        }
    }

    /**
     * 开始游戏
     *
     * @param entity 游戏实体
     * @return 数据
     */
    public static byte[] packStartGame(StartGameEntity entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_START_GAME);
            dos.writeInt(entity.gameID);
            dos.writeInt(entity.players.length);
            for (String imei : entity.players) {
                dos.writeUTF(imei);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    /**
     * 解析开始游戏
     *
     * @param data 数据
     * @return 游戏ID
     */
    public static StartGameEntity unpackStartGame(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            if (dis.readByte() == TYPE_START_GAME) {
                final int gameID = dis.readInt();
                final int length = dis.readInt();
                String[] players = new String[length];
                for (int i = 0; i < length; i++) {
                    players[i] = dis.readUTF();
                }
                return new StartGameEntity(gameID, players);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 准备状态
     */
    public static class PrepareEntity {
        public String imei;
        public boolean isPrepare;

        public PrepareEntity(String imei, boolean isPrepare) {
            this.imei = imei;
            this.isPrepare = isPrepare;
        }
    }

    public static byte[] packPrepareState(PrepareEntity entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_PREPARE);
            dos.writeUTF(entity.imei);
            dos.writeBoolean(entity.isPrepare);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static PrepareEntity unpackPrepareState(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            if (dis.readByte() == TYPE_PREPARE) {
                return new PrepareEntity(dis.readUTF(), dis.readBoolean());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
