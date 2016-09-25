package cn.muxiaozi.circle.main.version;

import android.content.Context;

import cn.muxiaozi.circle.base.BasePresenter;
import cn.muxiaozi.circle.base.BaseView;

/**
 * Created by 慕宵子 on 2016/7/30.
 */
public interface VersionContract {

    interface View extends BaseView{
        void showVersionDialog(boolean needUpdate, VersionInfo info);
    }

    abstract class Presenter extends BasePresenter<View>{
        public Presenter(Context context, View view) {
            super(context, view);
        }

        /**
         * 开始升级
         */
        public abstract void startUpgrade();

        /**
         * 检测更新
         */
        public abstract void checkUpgrade();
    }
}
