package cn.muxiaozi.circle.navigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.base.IConfig;
import cn.muxiaozi.circle.room.UserBean;
import cn.muxiaozi.circle.utils.ImageUtil;
import cn.muxiaozi.circle.utils.Config;

/**
 * Created by 慕宵子 on 2016/7/29.
 * <p/>
 * 设置个人信息
 */
public class SetInfoActivity extends AppCompatActivity {
    private static final int REQUEST_HEAD_IMG = 1;

    ImageView mHeadImg;
    EditText mName;
    EditText mAutograph;

    UserBean myInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_info);

        myInfo = Config.getMyInfo(this);
        initView();
    }

    private void initView() {
        initToolBar();

        LinearLayout selectHeadImg = (LinearLayout) findViewById(R.id.ll_select_head_img);
        selectHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SetInfoActivity.this, SelectHeadImg.class),
                        REQUEST_HEAD_IMG);
            }
        });

        Button submit = (Button) findViewById(R.id.btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(IConfig.CIRCLE_CONFIG, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(Config.HEAD_IMAGE, myInfo.getHeadImage());

                if (!TextUtils.isEmpty(mName.getText())) {
                    editor.putString(Config.NAME, mName.getText().toString());
                }
                if (!TextUtils.isEmpty(mAutograph.getText())) {
                    editor.putString(Config.AUTOGRAPH, mAutograph.getText().toString());
                }
                editor.apply();

                setResult(RESULT_OK);
                finish();
            }
        });

        mHeadImg = (ImageView) findViewById(R.id.iv_cover);
        Bitmap bitmap = ImageUtil.getHeadImg(this, myInfo.getHeadImage());
        if (bitmap != null) {
            mHeadImg.setImageBitmap(bitmap);
        }
        mName = (EditText) findViewById(R.id.edit_name);
        mName.setText(myInfo.getName());
        mAutograph = (EditText) findViewById(R.id.edit_autograph);
        mAutograph.setText(myInfo.getAutograph());
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        setTitle("编辑信息");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_HEAD_IMG:
                if (resultCode == RESULT_OK) {
                    final int headImgID = data.getIntExtra(Config.HEAD_IMAGE, 0);
                    myInfo.setHeadImage(headImgID);
                    Bitmap bitmap = ImageUtil.getHeadImg(this, headImgID);
                    if (bitmap != null) {
                        mHeadImg.setImageBitmap(bitmap);
                    }
                }
                break;
        }
    }
}
