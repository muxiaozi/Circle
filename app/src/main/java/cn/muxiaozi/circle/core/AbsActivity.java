package cn.muxiaozi.circle.core;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.View;

/**
 * Created by 慕宵子 on 2017/1/17 0017.
 */
public abstract class AbsActivity extends AppCompatActivity implements BaseView {
    private SparseIntArray mErrorString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mErrorString = new SparseIntArray();
    }

    public abstract void onPermissionsGranted(int requestCode);

    /**
     * 申请权限
     *
     * @param permissions 权限组
     * @param requestCode requestCode
     * @param stringId    申请理由
     */
    public void requestPermissions(final String[] permissions, final int requestCode, final int stringId) {
        mErrorString.put(requestCode, stringId);

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean showRequestPermission = false;

        for (String permission : permissions) {
            permissionCheck += ContextCompat.checkSelfPermission(this, permission);
            showRequestPermission = showRequestPermission || ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            onPermissionsGranted(requestCode);
        } else {
            if (showRequestPermission) {
                Snackbar.make(findViewById(android.R.id.content), stringId, Snackbar.LENGTH_INDEFINITE).setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(AbsActivity.this, permissions, requestCode);
                    }
                });
            } else {
                ActivityCompat.requestPermissions(AbsActivity.this, permissions, requestCode);
            }
        }

    }

    @Override
    public void showTips(String msg) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }
}
