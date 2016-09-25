package cn.muxiaozi.circle.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.muxiaozi.circle.R;
import cn.muxiaozi.circle.base.Constants;
import cn.muxiaozi.circle.game.link.LinkActivity;
import cn.muxiaozi.circle.room.RoomActivity;
import cn.muxiaozi.circle.utils.ToastUtil;
import cn.muxiaozi.circle.view.CarouselLayout;
import cn.muxiaozi.circle.view.RecycleViewDivider;

/**
 * Created by 慕宵子 on 2016/6/21.
 */
public class GameFragment extends Fragment implements GameContract.View {

    GameContract.Presenter mGamePresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGamePresenter = new GamePresenter(getContext(), this);

        initView();
    }

    private void initView() {
        View rootView = getView();
        RecyclerView gameList = (RecyclerView) rootView.findViewById(R.id.rv_game);
        gameList.setLayoutManager(new LinearLayoutManager(getContext()));
        gameList.addItemDecoration(new RecycleViewDivider(getContext(),
                LinearLayoutManager.HORIZONTAL));

        ArrayList<GameListBean> data = new ArrayList<>();
        data.add(new GameListBean(GameID.FLAPPY_BIRD, "FlappyBird", "和你的小伙伴一起玩FlappyBird吧！"));
        data.add(new GameListBean(GameID.LINK, "连连看", "和你的小伙伴一起玩连连看吧！"));
        GameListAdapter adapter = new GameListAdapter(getContext(), data);
        gameList.setAdapter(adapter);
        adapter.setOnItemClickListener(new GameListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int gameID) {
//                startGame(gameID);
                startActivity(new Intent(getContext(), LinkActivity.class));
            }
        });

        //添加轮播视图
        CarouselLayout carouselLayout = (CarouselLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.view_carousel, gameList, false);
        carouselLayout.setPage(new int[]{GameID.FLAPPY_BIRD});
        carouselLayout.setOnPageClickListener(new CarouselLayout.onPageClickListener() {
            @Override
            public void onClick(int gameID) {
                startGame(gameID);
            }
        });
        adapter.setHeaderView(carouselLayout);
    }

    private void startGame(int gameID) {
        Intent intent = new Intent(getContext(), RoomActivity.class);
        intent.putExtra(Constants.KEY_GAME_ID, gameID);
        getContext().startActivity(intent);
    }

    @Override
    public void showTips(String msg) {
        ToastUtil.showShort(getContext(), msg);
    }

    @Override
    public void onDestroy() {
        mGamePresenter.onDestroy();
        super.onDestroy();
    }
}
