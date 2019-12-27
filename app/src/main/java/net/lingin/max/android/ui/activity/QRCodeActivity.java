package net.lingin.max.android.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.king.zxing.CaptureHelper;
import com.king.zxing.ViewfinderView;

import net.lingin.max.android.R;
import net.lingin.max.android.logger.Log;
import net.lingin.max.android.ui.base.BaseActivity;
import net.lingin.max.android.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class QRCodeActivity extends BaseActivity {

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;

    @BindView(R.id.viewfinderView)
    ViewfinderView viewfinderView;

    @BindView(R.id.ivTorch)
    ImageView ivTorch;

    private CaptureHelper mCaptureHelper;

    @Override
    protected int onLayout() {
        return R.layout.activity_qrcode;
    }

    @Override
    protected void onObject() {
        mCaptureHelper = new CaptureHelper(this, surfaceView, viewfinderView, ivTorch);
        mCaptureHelper.playBeep(true)
                .vibrate(true)
                .tooDarkLux(50.0f)
                .brightEnoughLux(80.0f)
                .setOnCaptureCallback(result -> {
                    Log.i("扫码结果：" + result);
                    return false;
                }).onCreate();
    }

    @Override
    protected void onView() {

    }

    @Override
    protected void onData() {

    }

    @OnClick(R.id.iv_back)
    public void onBackClick(View view) {
        finish();
        overridePendingTransition(R.anim.slide_still, R.anim.slide_out_right);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCaptureHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCaptureHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCaptureHelper.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCaptureHelper.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
