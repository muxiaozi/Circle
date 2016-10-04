package cn.muxiaozi.circle.navigation;

/**
 * Created by 慕宵子 on 2016/7/28.
 */
class NavItemBean {
    static final int TYPE_NORMAL = 0;
    static final int TYPE_SEPARATOR = 1;

    private int icon;
    private String title;
    private String subHead;
    private int type;

    public NavItemBean(int type) {
        this.type = type;
    }

    NavItemBean(int icon, String title, String subHead, int type) {
        this.icon = icon;
        this.title = title;
        this.subHead = subHead;
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubHead() {
        return subHead;
    }

    public void setSubHead(String subHead) {
        this.subHead = subHead;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
