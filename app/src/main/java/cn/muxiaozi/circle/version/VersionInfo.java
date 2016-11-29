package cn.muxiaozi.circle.version;

/**
 * Created by 慕宵子 on 2016/8/30 0030.
 * Email: 1002042998@qq.com
 */
public class VersionInfo {
    //版本号
    private int versionCode;

    //版本信息
    private String version;

    //具体描述
    private String description;

    //下载链接
    private String url;

    //是否强制更新
    private boolean isForce;

    public VersionInfo() {
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "versionCode=" + versionCode +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", isForce=" + isForce +
                '}';
    }
}
