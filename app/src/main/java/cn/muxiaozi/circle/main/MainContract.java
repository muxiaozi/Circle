package cn.muxiaozi.circle.main;

import android.content.Context;
import android.net.wifi.ScanResult;

import java.util.List;

import cn.muxiaozi.circle.base.BasePresenter;
import cn.muxiaozi.circle.base.BaseView;

/**
 * Created by 慕宵子 on 2016/4/22.
 *
 * 契约类
 */
interface MainContract {

    abstract class Presenter extends BasePresenter<View>{
        Presenter(Context context, View view) {
            super(context, view);
        }

        /**
         * 开始打开wifi
         */
        public abstract void startJoin();

        /**
         * 开始连接到wifi热点
         * @param ssid 被连接的ssid
         */
        public abstract void startConnect(String ssid);

        /**
         * 开始打开wifi热点
         */
        public abstract void startInvite();

        /**
         * 获取附近wifi合法的wifi热点
         */
        public abstract List<ScanResult> getNearby();

        /**
         * 取消操作
         */
        public abstract void cancel();
    }

    interface View extends BaseView{

        void showProgressDialog(String content);

        void hideProgressDialog();

        void showNearby();

        void setFabMenuWorkState(boolean isWork);

        void showOpenGpsDialog();
    }
}
