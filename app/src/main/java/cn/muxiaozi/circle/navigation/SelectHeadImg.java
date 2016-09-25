package cn.muxiaozi.circle.navigation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.utils.ImageUtil;
import cn.muxiaozi.circle.utils.InfoUtil;

/**
 * Created by 慕宵子 on 2016/7/29.
 *
 * 选择头像
 */
public class SelectHeadImg extends AppCompatActivity {

    private int mItemWidth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_head_img);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mItemWidth = wm.getDefaultDisplay().getWidth() / 3;

        initView();
    }

    private void initView() {
        initToolBar();

        GridLayout content = (GridLayout) findViewById(R.id.gl_head_img);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, mItemWidth);
        for (int i = 0; i < 10; i++) {
            content.addView(makeHeadImg(i), params);
        }
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        setTitle("选择头像");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private ImageView makeHeadImg(final int id) {
        ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setPadding(4, 4, 4, 4);
        iv.setBackground(getResources().getDrawable(android.R.drawable.list_selector_background));
        Bitmap bm = ImageUtil.getHeadImg(this, id);
        if (bm != null) {
            iv.setImageBitmap(bm);
        } else {
            iv.setImageResource(R.mipmap.default_face);
        }
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(InfoUtil.HEAD_IMAGE, id);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        return iv;
    }
}
