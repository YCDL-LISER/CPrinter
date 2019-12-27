package net.lingin.max.android.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.king.zxing.Intents;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import net.lingin.max.android.R;
import net.lingin.max.android.ui.activity.QRCodeActivity;
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
    public void onSweepCodeClick(View view) {
        //跳转的默认扫码界面
        startActivityForResult(new Intent(getActivity(), QRCodeActivity.class), REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode && Activity.RESULT_OK == resultCode) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                String result = bundle.getString(Intents.Scan.RESULT);
                ToastUtils.show("解析结果:" + result, Toast.LENGTH_LONG);
            }
        }
    }
}
