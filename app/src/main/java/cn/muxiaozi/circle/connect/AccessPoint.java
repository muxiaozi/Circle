package cn.muxiaozi.circle.connect;

/**
 * Created by 慕宵子 on 2016/11/29 0029.
 *
 * 接入节点信息
 */
public class AccessPoint {

    //头像
    private int headPortrait;

    //姓名
    private String name;

    //接入点名称
    private String apName;

    public AccessPoint() {
    }

    public AccessPoint(int headPortrait, String name, String apName) {
        this.headPortrait = headPortrait;
        this.name = name;
        this.apName = apName;
    }

    public int getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(int headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApName() {
        return apName;
    }

    public void setApName(String apName) {
        this.apName = apName;
    }
}
