package cn.muxiaozi.circle.room;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.base.IConfig;
import cn.muxiaozi.circle.utils.ImageUtil;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 慕宵子 on 2016/7/24.
 * <p>
 * 显示玩家具体信息
 */
public class PlayerInfo extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);

        initView();
    }

    private void initView() {
        initToolBar("详细资料");
        CircleImageView headImg = (CircleImageView) findViewById(R.id.civ_img);
        TextView name = (TextView) findViewById(R.id.tv_name);
        TextView autograph = (TextView) findViewById(R.id.tv_autograph);

        UserBean myInfo = getIntent().getParcelableExtra(IConfig.KEY_USER_INFO);
        if (myInfo != null) {
            Bitmap bitmap = ImageUtil.getHeadImg(this, myInfo.getHeadImage());
            headImg.setImageBitmap(bitmap);
            name.setText(myInfo.getName());
            autograph.setText(myInfo.getAutograph());
        }
    }

    private void initToolBar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        setTitle(title);
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
