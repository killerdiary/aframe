package com.hy.app.ui.finger;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.View;

import com.hy.app.common.BaseActivity;
import com.hy.frame.util.MyLog;

/**
 * HY_Demo
 *
 * @author HeYan
 * @time 2017/5/11 17:00
 */
public class FingerActivity extends BaseActivity {
    private FingerprintManager manager;

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void initView() {}

    @Override
    public void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            manager.authenticate(null, new CancellationSignal(), 0, new FingerprintManager.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    MyLog.INSTANCE.e("onAuthenticationError");
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    MyLog.INSTANCE.e("onAuthenticationHelp");
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                    MyLog.INSTANCE.e("onAuthenticationSucceeded");
                }

                @Override
                public void onAuthenticationFailed() {
                    MyLog.INSTANCE.e("onAuthenticationFailed");
                }
            }, new Handler());
        } else {
            finish();
        }
    }


    public void requestData() {}


    public void updateUI() {}

    @Override
    public void onViewClick(View v) {}

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getCurContext(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_FINGERPRINT}, 0);
            }
        }
    }
}
