package cn.muxiaozi.circle.room;

import android.os.Parcel;
import android.os.Parcelable;

import cn.muxiaozi.circle.utils.Config;

/**
 * Created by 慕宵子 on 2016/7/18.
 * <p>
 * 用户信息实体
 */
public class UserBean implements Parcelable{

    /**
     * 唯一识别号
     */
    private String uniqueID;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private int headPortrait;

    public UserBean() {
    }

    public UserBean(String uniqueID, String name, int headPortrait) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.headPortrait = headPortrait;
    }

    protected UserBean(Parcel in) {
        uniqueID = in.readString();
        name = in.readString();
        headPortrait = in.readInt();
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

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uniqueID);
        dest.writeString(name);
        dest.writeInt(headPortrait);
    }
}
