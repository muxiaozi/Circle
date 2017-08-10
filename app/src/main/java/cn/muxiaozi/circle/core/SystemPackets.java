package cn.muxiaozi.circle.core;

import android.content.Context;
import android.content.Intent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cn.muxiaozi.circle.room.UserBean;

/**
 * Created by 慕宵子 on 2016/7/19.
 * <p>
 * 所有的系统数据类型均为负数
 */
class SystemPackets {
    public static final String ACTION_USER_IN = "circle.systempackets.action.USER_IN";
    public static final String ACTION_USER_OUT = "circle.systempackets.action.USER_OUT";

    public static final int PK_HEARBEAT = -1;
    public static final int PK_USER_IN = -2;
    public static final int PK_USER_OUT = -3;

    private static final AbsPacket[] mOperators = new AbsPacket[]{
            new PkHeartBeat(), new PkUserIn()
    };

    /**
     * 获取包的操作对象
     *
     * @param index 索引
     * @return 包操作对象
     * @throws IndexOutOfBoundsException type超出返回
     */
    static AbsPacket getPacketOperator(byte index) {
        return mOperators[-index - 1];
    }

    /**
     * 心跳数据包
     */
    static class PkHeartBeat extends AbsPacket {
        @Override
        protected void pack(DataOutputStream dos) throws IOException {

        }

        @Override
        public void unpack(DataInputStream dis) throws IOException {
        }

        @Override
        public void handle(Context context) {
        }
    }

    static class PkUserIn extends AbsPacket {
        public UserBean mUserBean = new UserBean();

        @Override
        protected void pack(DataOutputStream dos) throws IOException {
            dos.writeInt(mUserBean.getHeadPortrait());
            dos.writeUTF(mUserBean.getName());
            dos.writeUTF(mUserBean.getUniqueID());
        }

        @Override
        protected void unpack(DataInputStream dis) throws IOException {
            mUserBean.setHeadPortrait(dis.readInt());
            mUserBean.setName(dis.readUTF());
            mUserBean.setUniqueID(dis.readUTF());
        }

        @Override
        public void handle(Context context) {
            Intent i = new Intent(ACTION_USER_IN);
            i.putExtra("userbean", mUserBean);
            context.sendBroadcast(i);
        }
    }

    static class PkUserOut extends AbsPacket {
        public String mName;

        @Override
        protected void pack(DataOutputStream dos) throws IOException {
            dos.writeUTF(mName);
        }

        @Override
        protected void unpack(DataInputStream dis) throws IOException {
            mName = dis.readUTF();
        }

        @Override
        public void handle(Context context) {
            Intent i = new Intent(ACTION_USER_IN);
            i.putExtra("name", mName);
            context.sendBroadcast(i);
        }
    }

    //朋友加入
    public static final byte TYPE_FRIEND_IN = -2;

    //朋友离开
    public static final byte TYPE_FRIEND_OUT = -4;

    //与服务器连接断开
    public static final byte TYPE_DISCONNECT_SERVER = -6;

    //房间内准备
    public static final byte TYPE_PREPARE = -8;

    //开始游戏（丛游戏大厅到游戏界面）
    public static final byte TYPE_START_GAME = -10;

    /**
     * 打包心跳数据
     */
    static byte[] packHeartBeat() {
        byte[] data = new byte[1];
        data[0] = TYPE_HEART_BEAT;
        return data;
    }

    /**
     * 打包服务器关闭消息
     *
     * @return 数据
     */
    static byte[] packDisconnectServer() {
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
    static byte[] packFriendIn(UserBean userBean) {
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
    static UserBean unpackFriendIn(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        UserBean userBean = null;
        try {
            dis.readByte();
            userBean = new UserBean();
            userBean.setHeadImage(dis.readInt());
            userBean.setName(dis.readUTF());
            userBean.setAutograph(dis.readUTF());
            userBean.setImei(dis.readUTF());
            userBean.setPrepare(dis.readBoolean());
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
    static byte[] packFriendOut(String imei) {
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
    static String unpackFriendOut(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            dis.readByte();
            return dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
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
            dis.readByte();
            final int gameID = dis.readInt();
            final int length = dis.readInt();
            String[] players = new String[length];
            for (int i = 0; i < length; i++) {
                players[i] = dis.readUTF();
            }
            return new StartGameEntity(gameID, players);
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
            dis.readByte();
            return new PrepareEntity(dis.readUTF(), dis.readBoolean());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
