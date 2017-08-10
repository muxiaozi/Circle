package cn.muxiaozi.circle.room;

import java.util.ArrayList;

import cn.muxiaozi.circle.core.BaseView;
import cn.muxiaozi.circle.core.SystemPacket;

/**
 * Created by 慕宵子 on 2016/7/23.
 *
 * 游戏大厅契约类
 */
interface RoomView extends BaseView {
    ArrayList<UserBean> getPlayerList();

    void updatePlayerList();

    void startGame(SystemPacket.StartGameEntity entity);

    void exit();

    void setPrepare(boolean isPrepare);
}
