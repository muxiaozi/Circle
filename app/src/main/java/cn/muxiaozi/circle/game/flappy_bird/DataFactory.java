package cn.muxiaozi.circle.game.flappy_bird;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by 慕宵子 on 2016/8/8 0008.
 */
public abstract class DataFactory {

    /**
     * 开始游戏
     */
    public static final byte TYPE_START_GAME = 1;

    /**
     * 游戏结束
     */
    public static final byte TYPE_GAME_OVER = 2;

    /**
     * 小鸟用力飞
     */
    public static final byte TYPE_JUMP = 3;

    /**
     * 小鸟死亡
     */
    public static final byte TYPE_DIE = 4;

    /**
     * 产生管道
     */
    public static final byte TYPE_GENERATE_BAR = 5;

    /**
     * 飞过一个管道
     */
    public static final byte TYPE_OVER_BAR = 6;

    /**
     * 要求玩家准备，另一个作用是确保所有玩家正确开始游戏
     */
    public static final byte TYPE_REQUEST_PREPARE = 7;

    /**
     * 预备
     */
    public static final byte TYPE_READY = 8;

    /**
     * 进入加载界面，并准备完毕
     */
    public static final byte TYPE_PREPARE = 9;

    /**
     * 玩家进入游戏
     */
    public static byte[] packPrepare(String imei) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_PREPARE);
            dos.writeUTF(imei);
        } catch (IOException ignored) {
        }
        return baos.toByteArray();
    }

    public static String unpackPrepare(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            if (dis.readByte() == TYPE_PREPARE) {
                return dis.readUTF();
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * 开始游戏
     */
    public static byte[] packStartGame(String[] imeis) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_START_GAME);
            dos.writeByte(imeis.length);
            for (String imei : imeis) {
                dos.writeUTF(imei);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static String[] unpackStartGame(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        String[] imeis = null;
        try {
            if (dis.readByte() == TYPE_START_GAME) {
                imeis = new String[dis.readByte()];
                for (int i = 0; i < imeis.length; i++) {
                    imeis[i] = dis.readUTF();
                }
            }
        } catch (IOException ignored) {
        }
        return imeis;
    }

    /**
     * 游戏结束
     */
    public static byte[] packGameOver(int[] grades) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_GAME_OVER);
            dos.writeByte(grades.length);
            for (int grade : grades) {
                dos.writeInt(grade);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static int[] unpackGameOver(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        int[] grades = null;
        try {
            if (dis.readByte() == TYPE_GAME_OVER) {
                grades = new int[dis.readByte()];
                for (int i = 0; i < grades.length; i++) {
                    grades[i] = dis.readInt();
                }
            }
        } catch (IOException ignored) {
        }
        return grades;
    }

    /**
     * 玩家点击屏幕
     */
    public static byte[] packJump(JumpEntity jumpEntity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_JUMP);
            dos.writeUTF(jumpEntity.imei);
            dos.writeFloat(jumpEntity.y);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static JumpEntity unpackJump(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        JumpEntity jumpEntity = null;
        try {
            if (dis.readByte() == TYPE_JUMP) {
                jumpEntity = new JumpEntity(dis.readUTF(), dis.readFloat());
            }
        } catch (IOException ignored) {
        }
        return jumpEntity;
    }

    /**
     * 玩家小鸟碰壁挂掉
     */
    public static byte[] packDie(String imei) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_DIE);
            dos.writeUTF(imei);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static String unpackDie(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            if (dis.readByte() == TYPE_DIE) {
                return dis.readUTF();
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * 产生游戏管道
     */
    public static byte[] packGenerateBar(float downBarTopY) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_GENERATE_BAR);
            dos.writeFloat(downBarTopY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static float unpackGenerateBar(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            if (dis.readByte() == TYPE_GENERATE_BAR) {
                return dis.readFloat();
            }
        } catch (IOException ignored) {
        }
        return 0.0F;
    }

    /**
     * 玩家越过管道的一分
     */
    public static byte[] packOverBar(String imei) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(TYPE_OVER_BAR);
            dos.writeUTF(imei);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static String unpackOverBar(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            if (dis.readByte() == TYPE_OVER_BAR) {
                return dis.readUTF();
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * 要求玩家准备
     */
    public static byte[] packRequestPrepare() {
        byte[] data = new byte[1];
        data[0] = TYPE_REQUEST_PREPARE;
        return data;
    }

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

    public static class JumpEntity {
        public String imei;
        public float y;

        public JumpEntity(String imei, float y) {
            this.imei = imei;
            this.y = y;
        }
    }
}
