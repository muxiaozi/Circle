package cn.muxiaozi.circle.main;

import cn.muxiaozi.circle.core.BaseView;

/**
 * Created by 慕宵子 on 2016/4/22.
 */
interface MainView extends BaseView {

    void showProgressDialog(String content);

    void hideProgressDialog();

    void showNearby();

    void setFabMenuWorkState(boolean isWork);

    void showOpenGpsDialog();
}
