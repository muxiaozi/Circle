package cn.muxiaozi.circle.navigation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.base.Constants;

/**
 * Created by 慕宵子 on 2016/7/29.
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences mPreferences;

    private SwitchCompat switchFps;
    private SwitchCompat switchMusic;
    private SwitchCompat switchSounds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
    }

    private void initView() {
        initToolBar();

        findViewById(R.id.ll_fps).setOnClickListener(this);
        findViewById(R.id.ll_music).setOnClickListener(this);
        findViewById(R.id.ll_sounds).setOnClickListener(this);

        mPreferences = getSharedPreferences(Constants.CIRCLE_CONFIG, MODE_PRIVATE);

        switchFps = (SwitchCompat) findViewById(R.id.sc_fps);
        switchFps.setChecked(mPreferences.getBoolean(Constants.ConfigOption.FPS, false));

        switchMusic = (SwitchCompat) findViewById(R.id.sc_music);
        switchMusic.setChecked(mPreferences.getBoolean(Constants.ConfigOption.MUSIC, true));

        switchSounds = (SwitchCompat) findViewById(R.id.sc_sounds);
        switchSounds.setChecked(mPreferences.getBoolean(Constants.ConfigOption.SOUNDS, true));
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_fps:
                switchFps.setChecked(!switchFps.isChecked());
                break;
            case R.id.ll_music:
                switchMusic.setChecked(!switchMusic.isChecked());
                break;
            case R.id.ll_sounds:
                switchSounds.setChecked(!switchSounds.isChecked());
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putBoolean(Constants.ConfigOption.FPS, switchFps.isChecked());
        editor.putBoolean(Constants.ConfigOption.MUSIC, switchMusic.isChecked());
        editor.putBoolean(Constants.ConfigOption.SOUNDS, switchSounds.isChecked());

        editor.apply();
    }
}
