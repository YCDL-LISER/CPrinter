package net.lingin.max.android.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import net.lingin.max.android.R;
import net.lingin.max.android.ui.activity.BluetoothConnectActivity;
import net.lingin.max.android.ui.base.BaseFragment;

import butterknife.BindView;

public class InstallFragment extends BaseFragment {

    @BindView(R.id.groupListView)
    QMUIGroupListView mGroupListView;

    @Override
    protected int onLayout() {
        return R.layout.fragment_install;
    }

    @Override
    protected void onObject() {

    }

    @Override
    protected void onView() {
        QMUICommonListItemView connectionState = mGroupListView.createItemView("连接状态");
        connectionState.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        View.OnClickListener connectionStateClickListener = view -> {
            // 连接状态
//            connectionState.setDetailText("Print4");
            Intent intent = new Intent(getContext(), BluetoothConnectActivity.class);
            startActivity(intent);
        };

        QMUIGroupListView.newSection(getContext())
                .setTitle("基本设置")
                .addItemView(connectionState, connectionStateClickListener)
                .addTo(mGroupListView);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String macAddress = data.getStringExtra(BluetoothConnectActivity.EXTRA_DEVICE_ADDRESS);
        }
    }
}
