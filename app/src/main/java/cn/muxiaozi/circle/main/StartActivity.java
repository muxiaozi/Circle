package cn.muxiaozi.circle.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;

import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.utils.InfoUtil;

/**
 * Created by 慕宵子 on 2016/7/29.
 */
public class StartActivity extends AppCompatActivity {

    private static final int REQUEST_READ_PHONE_STATE = 1;

    SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        preferences = getSharedPreferences(Constants.CIRCLE_CONFIG, MODE_PRIVATE);
        boolean isFirstRun = preferences.getBoolean(Constants.ConfigOption.FIRST_RUN, true);
        if (isFirstRun) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
            } else {
                initConfig();
                startActivity(new Intent(this, MainActivity.class));
                this.finish();
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }
    }

    /**
     * 初始化配置信息
     */
    private void initConfig() {
        String imei = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(Constants.ConfigOption.FIRST_RUN, false);
        editor.putInt(InfoUtil.HEAD_IMAGE, 0);
        editor.putString(InfoUtil.IMEI, imei);
        editor.putString(InfoUtil.NAME, android.os.Build.MODEL);
        editor.putString(InfoUtil.AUTOGRAPH, "我的圈圈我做主！");

        editor.putBoolean(Constants.ConfigOption.FPS, false);
        editor.putBoolean(Constants.ConfigOption.MUSIC, true);
        editor.putBoolean(Constants.ConfigOption.SOUNDS, true);

        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //获取本机的IMEI串号
                    initConfig();
                    startActivity(new Intent(this, MainActivity.class));
                }
                this.finish();
                break;
            default:
                break;
        }

    }
}
