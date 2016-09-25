package cn.muxiaozi.circle.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.game.GameFragment;
import cn.muxiaozi.circle.main.version.VersionContract;
import cn.muxiaozi.circle.main.version.VersionInfo;
import cn.muxiaozi.circle.main.version.VersionPresenter;
import cn.muxiaozi.circle.navigation.AboutActivity;
import cn.muxiaozi.circle.navigation.FeedBackActivity;
import cn.muxiaozi.circle.navigation.HelpActivity;
import cn.muxiaozi.circle.navigation.NavigationAdapter;
import cn.muxiaozi.circle.navigation.SetInfoActivity;
import cn.muxiaozi.circle.navigation.SettingActivity;
import cn.muxiaozi.circle.room.UserBean;
import cn.muxiaozi.circle.utils.ImageUtil;
import cn.muxiaozi.circle.utils.InfoUtil;
import cn.muxiaozi.circle.utils.NetWorkUtil;
import cn.muxiaozi.circle.view.FloatingActionMenu;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements MainContract.View, VersionContract.View {
    private MainContract.Presenter mMainPresenter;
    private VersionContract.Presenter mVersionPresenter;

    private DrawerLayout mMainLayout;
    private FloatingActionMenu mFabMenu;
    private ProgressDialog mProgressDialog;

    private static final int REQUEST_MY_INFO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainPresenter = new MainPresenter(this, this);
        mVersionPresenter = new VersionPresenter(this, this);

        //初始化网络状态管理类
        NetWorkUtil.init(this);

        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MY_INFO:
                if (resultCode == RESULT_OK) {
                    initMyInfo();
                }
                break;
        }
    }

    private void initView() {
        initToolBar();
        initNavigation();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        mMainLayout = (DrawerLayout) findViewById(R.id.draw_main);
        mFabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        setUpWithFabMenu(mFabMenu);

        //加载内容区域
        initContent();
    }

    /**
     * 初始化内容区域
     */
    private void initContent() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        final GameFragment gameFragment = new GameFragment();
        transaction.add(R.id.frame_main, gameFragment);
        transaction.commit();
    }

    /**
     * 初始化ToolBar
     */
    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_navigation);
        setSupportActionBar(toolbar);
        setTitle("我的圈圈");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 初始化个人信息
     */
    private void initMyInfo() {
        UserBean myInfo = InfoUtil.getMyInfo(this);
        CircleImageView headImg = (CircleImageView) findViewById(R.id.civ_img);
        Bitmap bitmap = ImageUtil.getHeadImg(this, myInfo.getHeadImage());
        if (bitmap != null) {
            headImg.setImageBitmap(bitmap);
        }
        TextView name = (TextView) findViewById(R.id.tv_name);
        name.setText(myInfo.getName());
        TextView autograph = (TextView) findViewById(R.id.tv_autograph);
        autograph.setText(myInfo.getAutograph());
    }

    /**
     * 初始化导航栏
     */
    private void initNavigation() {
        initMyInfo();

        RecyclerView navList = (RecyclerView) findViewById(R.id.rv_navigation);
        navList.setLayoutManager(new LinearLayoutManager(this));
        NavigationAdapter adapter = new NavigationAdapter(this);
        navList.setAdapter(adapter);

        adapter.setOnNavItemClickListener(new NavigationAdapter.OnNavItemClickListener() {
            @Override
            public void onClick(final String title) {
                mMainLayout.closeDrawer(GravityCompat.START);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (title) {
                            case "个人资料":
                                startActivityForResult(new Intent(MainActivity.this, SetInfoActivity.class), REQUEST_MY_INFO);
                                break;
                            case "设置":
                                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                                break;
                            case "帮助":
                                startActivity(new Intent(MainActivity.this, HelpActivity.class));
                                break;
                            case "版本更新":
                                mMainLayout.closeDrawer(GravityCompat.START);
                                mVersionPresenter.checkUpgrade();
                                break;
                            case "意见反馈":
                                startActivity(new Intent(MainActivity.this, FeedBackActivity.class));
                                break;
                            case "关于圈圈":
                                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                                break;
                        }
                    }
                }, 300);
            }
        });
    }

    private void setUpWithFabMenu(FloatingActionMenu menu) {
        menu.setOnMenuItemClickListener(new FloatingActionMenu.OnItemClickListener() {
            @Override
            public void onItemClick(int action) {
                switch (action) {
                    case ACTION_INVITE:
                        mMainPresenter.startInvite();
                        break;

                    case ACTION_JOIN:
                        mMainPresenter.startJoin();
                        break;

                    case ACTION_CANCEL:
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("确定要断开连接？")
                                .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mFabMenu.cancelWork();
                                        mMainPresenter.cancel();
                                    }
                                })
                                .setNegativeButton("不", null)
                                .show();
                        break;
                }
            }
        });
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if (mMainLayout.isDrawerOpen(GravityCompat.START)) {
            mMainLayout.closeDrawer(GravityCompat.START);
        } else if (mFabMenu.onBackPressed()) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showTips("再按一次退出程序！");
                exitTime = System.currentTimeMillis();
            } else {
                mMainPresenter.cancel();
                finish();
            }
        }
    }

    @Override
    public void showProgressDialog(String content) {
        mProgressDialog.setMessage(content);
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    private boolean needScanWifi;

    @Override
    public void showNearby() {
        needScanWifi = true;

        final List<String> searchResult = new ArrayList<>(10);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.simple_text_item, R.id.tv_text, searchResult);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("正在搜索圈圈...")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMainPresenter.startConnect("shw" + adapter.getItem(which));
                        needScanWifi = false;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        needScanWifi = false;
                    }
                })
                .setCancelable(false)
                .show();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                List<ScanResult> scanDatas = mMainPresenter.getNearby();
                if (scanDatas != null) {
                    adapter.clear();
                    for (ScanResult scan : scanDatas) {
                        if (scan.SSID.startsWith("shw")) {
                            adapter.add(scan.SSID.substring(3));
                        }
                    }
                }
                if (needScanWifi)
                    new Handler().postDelayed(this, 3000);
            }
        });
    }

    @Override
    public void showTips(String msg) {
        Snackbar.make(mMainLayout, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setFabMenuWorkState(boolean isWork) {
        if (isWork) {
            mFabMenu.startWork();
        } else {
            mFabMenu.cancelWork();
        }
    }

    @Override
    public void showVersionDialog(boolean needUpdate, VersionInfo info) {
        if (needUpdate) {
            new AlertDialog.Builder(this).
                    setTitle("新版本【V" + info.getVersion() + "】可用")
                    .setMessage("【更新内容】" + info.getDescription())
                    .setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mVersionPresenter.startUpgrade();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("下次再说", null)
                    .show();
        } else {
            showTips("已经是最新版本了");
        }
    }

    @Override
    protected void onDestroy() {
        mVersionPresenter.onDestroy();
        mMainPresenter.onDestroy();
        super.onDestroy();
        System.exit(0);
    }
}
