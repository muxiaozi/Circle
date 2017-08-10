package cn.muxiaozi.circle.game;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.core.CoreService;
import cn.muxiaozi.circle.net.IReceiver;

/**
 * Created by 慕宵子 on 2016/10/1 0001.
 */

public class speedTest extends AppCompatActivity implements IReceiver {
    private CoreService.MessageBinder mDeliver;
    boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedtest);

        bindService(new Intent(this, CoreService.class), conn, BIND_AUTO_CREATE);

        if (CoreService.isServer()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRunning) {
                        if (mDeliver != null) {
                            mDeliver.send(packFriendOut(System.currentTimeMillis()));
                            LogUtil.d("send:" + System.currentTimeMillis());
                        }
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    /**
     * 打包朋友退出信息
     *
     * @param imei 朋友识别号
     * @return 数据
     */
    byte[] packFriendOut(long imei) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeByte(1);
            dos.writeLong(imei);
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
    long unpackFriendOut(byte[] data) {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        try {
            dis.readByte();
            return dis.readLong();
        } catch (IOException ignored) {
        }
        return 0;
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDeliver = (CoreService.MessageBinder) service;
            mDeliver.addObserver(speedTest.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mDeliver != null) {
                mDeliver.removeObserver(speedTest.this);
            }
        }
    };

    @Override
    public void receive(byte[] data) {
        LogUtil.d("receive:" + System.currentTimeMillis() + "," + unpackFriendOut(data));
        if (CoreService.isServer()) {
            LogUtil.d("fps:" + (System.currentTimeMillis() - unpackFriendOut(data)));
        } else {
            if (mDeliver != null) {
                mDeliver.send(packFriendOut(unpackFriendOut(data)));
            }
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        isRunning = false;
        super.onDestroy();
    }
}
