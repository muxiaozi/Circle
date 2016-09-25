package cn.muxiaozi.circle.game;

/**
 * Created by 慕宵子 on 2016/7/11.
 */
public class GameListBean {

    private int gameID;
    private String title;
    private String detail;

    public GameListBean() {
    }

    public GameListBean(int gameID, String title, String detail) {
        this.gameID = gameID;
        this.title = title;
        this.detail = detail;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
