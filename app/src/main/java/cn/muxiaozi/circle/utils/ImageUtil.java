package cn.muxiaozi.circle.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 慕宵子 on 2016/7/29.
 */
public class ImageUtil {

    /**
     * 获取游戏封面
     * @param gameID 游戏ID
     */
    public static Bitmap getGameCover(Context context, int gameID) {
        AssetManager manager = context.getAssets();
        return getGameCover(manager, gameID);
    }
    public static Bitmap getGameCover(AssetManager manager, int gameID) {
        Bitmap bitmap;
        try {
            InputStream is = manager.open("game_icon/cover" + String.valueOf(gameID) + ".png");
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * 获取游戏图标
     * @param gameID 游戏ID
     */
    public static Bitmap getGameIcon(Context context, int gameID) {
        AssetManager manager = context.getAssets();
        return getGameIcon(manager, gameID);
    }
    public static Bitmap getGameIcon(AssetManager manager, int gameID) {
        Bitmap bitmap;
        try {
            InputStream is = manager.open("game_icon/icon" + String.valueOf(gameID) + ".png");
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * 获取玩家头像
     * @param imgID 头像ID
     */
    public static Bitmap getHeadImg(Context context, int imgID) {
        AssetManager manager = context.getAssets();
        return getHeadImg(manager, imgID);
    }
    public static Bitmap getHeadImg(AssetManager manager, int imgID) {
        Bitmap bitmap;
        try {
            InputStream is = manager.open("face/face" + String.valueOf(imgID) + ".png");
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }
}
