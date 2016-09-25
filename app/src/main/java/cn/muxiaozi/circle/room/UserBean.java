package cn.muxiaozi.circle.room;

import android.os.Parcel;
import android.os.Parcelable;

import cn.muxiaozi.circle.utils.InfoUtil;

/**
 * Created by 慕宵子 on 2016/7/18.
 * <p>
 * 用户信息实体
 */
public class UserBean implements Parcelable {

    /**
     * 头像
     */
    private int headImage;
    /**
     * 姓名
     */
    private String name;
    /**
     * 个性签名
     */
    private String autograph;

    /**
     * imei手机识别号
     */
    private String imei;

    /**
     * 是否准备
     */
    private boolean isPrepare;

    public UserBean() {
        this.imei = InfoUtil.DEFAULT_IMEI;
        this.isPrepare = false;
    }

    public UserBean(int headImage, String name, String autograph, String imei) {
        this.headImage = headImage;
        this.name = name;
        this.autograph = autograph;
        this.imei = imei;
        this.isPrepare = false;
    }

    protected UserBean(Parcel in) {
        headImage = in.readInt();
        name = in.readString();
        autograph = in.readString();
        imei = in.readString();
        isPrepare = in.readByte() != 0;
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    public int getHeadImage() {
        return headImage;
    }

    public void setHeadImage(int headImage) {
        this.headImage = headImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAutograph() {
        return autograph;
    }

    public void setAutograph(String autograph) {
        this.autograph = autograph;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public boolean isPrepare() {
        return isPrepare;
    }

    public void setPrepare(boolean prepare) {
        isPrepare = prepare;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(headImage);
        dest.writeString(name);
        dest.writeString(autograph);
        dest.writeString(imei);
        dest.writeByte((byte) (isPrepare ? 1 : 0));
    }
}
