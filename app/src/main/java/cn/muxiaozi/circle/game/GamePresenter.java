package cn.muxiaozi.circle.game;

import android.content.Context;

/**
 * Created by 慕宵子 on 2016/7/19.
 */
public class GamePresenter extends GameContract.Presenter {

    public GamePresenter(Context context, GameContract.View view) {
        super(context, view);
    }

    @Override
    public void onDestroy() {

    }
}
