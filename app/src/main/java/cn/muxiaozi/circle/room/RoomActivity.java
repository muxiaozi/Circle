package cn.muxiaozi.circle.room;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.game.GameID;
import cn.muxiaozi.circle.game.flappy_bird.FlappyBirdActivity;
import cn.muxiaozi.circle.game.link.LinkActivity;
import cn.muxiaozi.circle.game.speedTest;
import cn.muxiaozi.circle.net.DataFactory;
import cn.muxiaozi.circle.net.DataService;
import cn.muxiaozi.circle.utils.ToastUtil;
import cn.muxiaozi.circle.view.RecycleViewDivider;

/**
 * Created by 慕宵子 on 2016/7/23.
 * <p/>
 * 游戏大厅
 */
public class RoomActivity extends AppCompatActivity implements RoomContract.View,
        View.OnClickListener {
    private RoomContract.Presenter mRoomPresenter;

    private int mGameID;

    //是否为服务端
    private boolean isServer = false;

    //是否已经准备
    private boolean isPrepare = false;

    //开始按钮
    private Button mBtnStart;

    //玩家列表和列表适配器
    private ArrayList<UserBean> mPlayers;

    private PlayerAdapter mPlayerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        initView();

        mRoomPresenter = new RoomPresenter(this, this);
    }

    private void initView() {
        initToolBar("游戏大厅");

        mBtnStart = (Button) findViewById(R.id.btn_prepare);
        if(mBtnStart != null){
            mBtnStart.setOnClickListener(this);
        }

        mGameID = getIntent().getIntExtra(Constants.KEY_GAME_ID, GameID.NULL);

        isServer = DataService.isServer();
        if (isServer) {
            mBtnStart.setText("开始游戏");
        } else {
            mBtnStart.setText("准备");
        }

        RecyclerView playerList = (RecyclerView) findViewById(R.id.rv_player);
        playerList.setLayoutManager(new LinearLayoutManager(this));
        playerList.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));
        mPlayers = new ArrayList<>(8);
        mPlayerAdapter = new PlayerAdapter(this, mPlayers);
        playerList.setAdapter(mPlayerAdapter);
        setUpPlayerListClick(mPlayerAdapter);
    }

    private void setUpPlayerListClick(PlayerAdapter adapter) {
        adapter.setOnItemClickListener(new PlayerAdapter.OnItemClickListener() {
            @Override
            public void onClick(UserBean player) {
                Intent intent = new Intent(RoomActivity.this, PlayerInfo.class);
                intent.putExtra(Constants.KEY_USER_INFO, player);
                startActivity(intent);
            }
        });
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

    @Override
    public void onClick(View v) {
        if (isServer) {
            if (mRoomPresenter.isAllPlayerPrepared()) {
                //生成玩家IMEI列表
                String[] players = new String[mPlayers.size()];
                for (int i = 0; i < mPlayers.size(); i++) {
                    players[i] = mPlayers.get(i).getImei();
                }
                //构造开始游戏实体
                DataFactory.StartGameEntity entity =
                        new DataFactory.StartGameEntity(mGameID, players);
                //通知其他人开始游戏
                mRoomPresenter.startGame(entity);
                startGame(entity);
            } else {
                showTips("请等待所有玩家准备就绪！");
            }
        } else {
            if (isPrepare) {
                mRoomPresenter.cancelPrepare();
                isPrepare = false;
                mBtnStart.setText("准备");
            } else {
                mRoomPresenter.prepare();
                isPrepare = true;
                mBtnStart.setText("取消准备");
            }
        }
    }

    @Override
    public ArrayList<UserBean> getPlayerList() {
        return mPlayers;
    }

    @Override
    public void updatePlayerList() {
        mPlayerAdapter.notifyDataSetChanged();
    }

    @Override
    public void startGame(DataFactory.StartGameEntity entity) {
        Intent intent;
        switch (entity.gameID) {
            case GameID.FIND_ME:
                intent = null;
                break;
            case GameID.DOODLE:
                intent = new Intent(this, speedTest.class);
                break;
            case GameID.FLAPPY_BIRD:
                intent = new Intent(this, FlappyBirdActivity.class);
                break;
            case GameID.LINK:
                intent = new Intent(this, LinkActivity.class);
                break;
            default:
                intent = null;
        }

        if (intent != null) {
            intent.putExtra(Constants.KEY_USER_INFO, entity.players);
            startActivity(intent);
        }

        finish();
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void setPrepare(boolean isPrepare) {
        this.isPrepare = isPrepare;
        if (isPrepare) {
            mBtnStart.setText("取消准备");
        } else {
            mBtnStart.setText("准备");
        }
    }

    @Override
    protected void onDestroy() {
        mRoomPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void showTips(String msg) {
        ToastUtil.showShort(this, msg);
    }
}
