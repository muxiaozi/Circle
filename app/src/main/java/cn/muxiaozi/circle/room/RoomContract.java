package cn.muxiaozi.circle.room;

import android.content.Context;

import java.util.ArrayList;

import cn.muxiaozi.circle.base.BasePresenter;
import cn.muxiaozi.circle.base.BaseView;
import cn.muxiaozi.circle.net.DataFactory;

/**
 * Created by 慕宵子 on 2016/7/23.
 *
 * 游戏大厅契约类
 */
interface RoomContract {

    abstract class Presenter extends BasePresenter<View> {

        Presenter(Context context, View view) {
            super(context, view);
        }

        public abstract void prepare();

        public abstract void cancelPrepare();

        public abstract void startGame(DataFactory.StartGameEntity entity);

        public abstract boolean isAllPlayerPrepared();
    }

    interface View extends BaseView {
        ArrayList<UserBean> getPlayerList();

        void updatePlayerList();

        void startGame(DataFactory.StartGameEntity entity);

        void exit();

        void setPrepare(boolean isPrepare);
    }
}
