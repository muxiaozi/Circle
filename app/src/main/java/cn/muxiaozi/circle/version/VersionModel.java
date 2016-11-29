package cn.muxiaozi.circle.version;

import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import cn.muxiaozi.circle.base.IConfig;
import cn.muxiaozi.circle.utils.HttpUtil;

/**
 * Created by 慕宵子 on 2016/5/23.
 * <p/>
 * 版本获取和下载
 */
class VersionModel {
    static final int WHAT_UPDATE = 1;    //更新进度
    static final int WHAT_SUCCESS = 2;   //更新成功
    static final int WHAT_FAIL = 3;      //更新失败

    private boolean isDownloading;  //是否正在更新

    //版本信息
    private static VersionInfo mVersionInfo;

    VersionModel() {
        mVersionInfo = new VersionInfo();
        isDownloading = false;
    }

    /**
     * 获取最新版本信息
     *
     * @param callBack 新版本信息接口回调
     */
    void getNewestVersionInfo(final VersionInfoCallBack callBack) {
        final HashMap<String, String> requestParams = new HashMap<>(1);
        requestParams.put("key", "141022");
        HttpUtil.post(IConfig.Urls.VERSION, requestParams, new HttpUtil.HttpResponseCallBack() {
            @Override
            public void onSuccess(String result) {
                String results[] = result.split("#");
                mVersionInfo.setVersion(results[0]);
                mVersionInfo.setUrl(IConfig.Urls.QINIU_HOST + results[0] + IConfig.Urls.APK_EXT);
                mVersionInfo.setDescription(results[1]);
                callBack.onResult(mVersionInfo);
            }

            @Override
            public void onFailure(String result, Exception e) {
                callBack.onResult(null);
            }
        });

    }

    interface VersionInfoCallBack {
        void onResult(VersionInfo info);
    }

    /**
     * 下载是否正在进行
     *
     * @return 下载状态
     */
    boolean isDownloading() {
        return isDownloading;
    }

    /**
     * 下载最新应用程序
     *
     * @param handler 接受下载结果
     */
    void downloadAPK(final Handler handler) {
        if (mVersionInfo.getUrl() != null) {
            if (!isDownloading) {
                isDownloading = true;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL apkUrl = new URL(mVersionInfo.getUrl());
                            HttpURLConnection conn = (HttpURLConnection) apkUrl.openConnection();
                            conn.setConnectTimeout(2000);

                            final int apkSize = conn.getContentLength();    //文件总大小
                            int downloadSize = 0;   //已下载文件大小
                            int progress = 0;
                            handler.obtainMessage(WHAT_UPDATE, progress, 0).sendToTarget();

                            //拼装最新版本路径
                            StringBuilder apkPath = new StringBuilder(64);
                            apkPath.append(Environment.getExternalStorageDirectory().getAbsolutePath())
                                    .append(File.separator)
                                    .append("Circle_V")
                                    .append(mVersionInfo.getVersion())
                                    .append(".apk");

                            FileOutputStream fos = new FileOutputStream(apkPath.toString());
                            InputStream is = conn.getInputStream();
                            byte[] data = new byte[2048];
                            int len;
                            while ((len = is.read(data, 0, 2048)) != -1) {
                                fos.write(data, 0, len);
                                downloadSize += len;
                                if ((int) ((float) downloadSize / apkSize * 100) != progress) {
                                    progress = (int) ((float) downloadSize / apkSize * 100);
                                    handler.obtainMessage(WHAT_UPDATE, progress, 0).sendToTarget();
                                }
                            }

                            fos.close();
                            conn.disconnect();

                            handler.obtainMessage(WHAT_SUCCESS, apkPath.toString()).sendToTarget();
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.obtainMessage(WHAT_FAIL, e).sendToTarget();
                        } finally {
                            isDownloading = false;
                        }
                    }
                }).start();
            }
        }
    }
}
