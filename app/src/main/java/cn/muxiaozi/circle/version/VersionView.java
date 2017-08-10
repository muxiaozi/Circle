package cn.muxiaozi.circle.version;

import cn.muxiaozi.circle.core.BaseView;

/**
 * Created by 慕宵子 on 2016/7/30.
 */
interface VersionView extends BaseView {
    /**
     * 展示版本更新对话框
     *
     * @param needUpdate
     * @param info
     */
    void showVersionDialog(boolean needUpdate, VersionInfo info);
}
