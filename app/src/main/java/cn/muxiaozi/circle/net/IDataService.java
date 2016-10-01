package cn.muxiaozi.circle.net;

import cn.muxiaozi.circle.room.UserBean;

/**
 * Created by 慕宵子 on 2016/4/23.
 * <p>
 * 这个接口用于处理接收到的各种消息
 */
interface IDataService extends IReceiver {

    /**
     * 获取用户信息接口
     */
    interface IGetUserInfo {
        void onGet(UserBean bean);
    }

    /**
     * 委托消息服务获取自己的信息
     *
     * @param receiver 信息接受接口
     */
    void getMyInfo(IGetUserInfo receiver);

    /**
     * 委托消息服务获取在线朋友的信息
     *
     * @param receiver 信息接收接口
     */
    void getOnlineUserInfo(IGetUserInfo receiver);
}
