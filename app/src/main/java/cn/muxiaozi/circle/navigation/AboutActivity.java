package cn.muxiaozi.circle.navigation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.utils.Config;

/**
 * Created by 慕宵子 on 2016/7/29.
 */
public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initView();
    }

    private void initView() {
        initToolBar();

        String version = Config.getVersion(this);
        TextView txtVersion = (TextView) findViewById(R.id.tv_version);
        txtVersion.setText("V " + version);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        setTitle("关于圈圈");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
