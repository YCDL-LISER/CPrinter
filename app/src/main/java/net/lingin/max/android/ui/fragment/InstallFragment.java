package net.lingin.max.android.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import net.lingin.max.android.R;
import net.lingin.max.android.ui.activity.BluetoothConnectActivity;
import net.lingin.max.android.ui.base.BaseFragment;

import butterknife.BindView;

public class InstallFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.groupListView)
    QMUIGroupListView mGroupListView;

    @Override
    protected int onLayout() {
        return R.layout.fragment_install;
    }

    @Override
    protected void onView() {
        initTopBar();

        QMUICommonListItemView connectionState = mGroupListView.createItemView("连接状态");
        connectionState.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        View.OnClickListener connectionStateClickListener = view -> {
            Intent intent = new Intent(getContext(), BluetoothConnectActivity.class);
            startActivity(intent);
        };

        QMUIGroupListView.newSection(getContext())
                .setTitle("基本设置")
                .addItemView(connectionState, connectionStateClickListener)
                .addTo(mGroupListView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
//            String macAddress = data.getStringExtra(BluetoothConnectActivity.EXTRA_DEVICE_ADDRESS);
        }
    }

    private void initTopBar() {
        mTopBar.setTitle("设置");
    }

}
