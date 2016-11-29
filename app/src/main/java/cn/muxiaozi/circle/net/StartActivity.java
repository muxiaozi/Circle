package cn.muxiaozi.circle.net;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cn.muxiaozi.circle.main.MainActivity;
import cn.muxiaozi.circle.utils.Config;
import cn.muxiaozi.circle.welcome.WelcomeActivity;

/**
 * Created by 慕宵子 on 2016/7/29.
 */
public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.init(this);
        if (Config.isFirstRun()) {    //如果是第一次运行
            startActivity(new Intent(this, WelcomeActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        this.finish();
    }
}
