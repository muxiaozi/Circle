package cn.muxiaozi.circle.core;

import android.content.Context;

/**
 * Created by 慕宵子 on 2016/7/30.
 *
 * 基础指挥类
 */
public abstract class BasePresenter<T extends BaseView> {
    protected Context mContext;
    protected T mView;

    public BasePresenter(Context context, T view){
        mContext = context;
        mView = view;
    }

    public void onDestroy(){
    }
}
