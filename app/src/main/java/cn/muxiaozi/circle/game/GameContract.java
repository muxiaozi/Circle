package cn.muxiaozi.circle.game;

import android.content.Context;

import cn.muxiaozi.circle.core.BasePresenter;
import cn.muxiaozi.circle.core.BaseView;

/**
 * Created by 慕宵子 on 2016/7/19.
 */
public interface GameContract {
    abstract class Presenter extends BasePresenter<View>{
        public Presenter(Context context, View view) {
            super(context, view);
        }
    }

    interface View extends BaseView{
    }
}
