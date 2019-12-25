package net.lingin.max.android.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.xuexiang.xqrcode.XQRCode;

import net.lingin.max.android.R;
import net.lingin.max.android.ui.base.BaseFragment;
import net.lingin.max.android.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class BillFragment extends BaseFragment {

    private static final String TAG = BillFragment.class.getName();

    public static final int REQUEST_CODE = 1225;

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @Override
    protected int onLayout() {
        return R.layout.fragment_bill;
    }

    @Override
    protected void onView() {
        // 申请权限
        requestSelfPermission(new String[]{Manifest.permission.CAMERA}, (authorize, permissions) -> {
            Log.i(TAG, permissions.toString());
        });

        initTopBar();
    }

    private void initTopBar() {
        mTopBar.setTitle("票据");
    }

    @OnClick(R.id.sweepCode)
    public void onClick(View view) {
        XQRCode.startScan(this, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_SUCCESS) {
                    String result = bundle.getString(XQRCode.RESULT_DATA);
                    ToastUtils.show("解析结果:" + result, Toast.LENGTH_LONG);
                } else if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_FAILED) {
                    ToastUtils.show("解析二维码失败", Toast.LENGTH_LONG);
                }
            }
        }
    }
}
