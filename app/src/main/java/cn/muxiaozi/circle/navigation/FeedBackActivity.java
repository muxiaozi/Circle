package cn.muxiaozi.circle.navigation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.utils.ToastUtil;

/**
 * Created by 慕宵子 on 2016/7/29.
 */
public class FeedBackActivity extends AppCompatActivity {
    private EditText mContent;
    private EditText mTelephone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initViews();
    }

    /**
     * 初始化ToolBar
     *
     * @param title 标题
     */
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

    private void initViews() {
        initToolBar("意见反馈");

        mContent = (EditText) findViewById(R.id.edit_content);
        mTelephone = (EditText) findViewById(R.id.edit_contact);

        Button btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContent.getText().toString().isEmpty()) {
                    ToastUtil.showShort(FeedBackActivity.this, "请写下您的宝贵意见！");
                } else {
                    ToastUtil.showShort(FeedBackActivity.this, "感谢您的宝贵意见！");
                    finish();
                }
            }
        });
    }

}
