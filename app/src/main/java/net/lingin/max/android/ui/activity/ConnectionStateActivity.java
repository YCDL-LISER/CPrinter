package net.lingin.max.android.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import net.lingin.max.android.R;
import net.lingin.max.android.service.BluetoothClientFactory;
import net.lingin.max.android.ui.base.BaseActivity;
import net.lingin.max.android.utils.ToastUtils;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

public class ConnectionStateActivity extends BaseActivity {

    private static final String TAG = ConnectionStateActivity.class.getName();

    public static final String EXTRA_DEVICE_ADDRESS = "address";

    public static final int REQUEST_ENABLE_BT = 2;

    private BluetoothClient mClient;

    @BindView(R.id.blueToothGroupListView)
    QMUIGroupListView blueToothGroupListView;

    @BindView(R.id.bluetoothScan)
    Button bluetoothScan;

    private QMUIGroupListView.Section pairedSection;

    private QMUIGroupListView.Section searchedSection;

    private SearchRequest searchRequest;

    private SearchResponse searchResponse;

    private Set<String> removeDuplication = new HashSet<>(16);

    @Override
    protected int onLayout() {
        return R.layout.activity_bluetooth_connect;
    }

    @Override
    protected void onObject() {
        // 申请权限
        requestSelfPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, (authorize, permissions) -> {
            Log.i(TAG, permissions.toString());
        });

        mClient = BluetoothClientFactory.getBluetoothClient(this);

        // 初始化Section对象
        initSection();

        searchRequest = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build();

        searchResponse = new SearchResponse() {

            @Override
            public void onSearchStarted() {
                bluetoothScan.setText("停止");
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                boolean isNotDuplication = removeDuplication.add(device.getAddress());
                if (isNotDuplication) {
                    addListItemView(searchedSection, device, searchedClickListener);
                    addToListView(searchedSection);
                }
            }

            @Override
            public void onSearchStopped() {
                bluetoothScan.setText("扫描");
            }

            @Override
            public void onSearchCanceled() {
                bluetoothScan.setText("扫描");
            }
        };
    }

    @Override
    protected void onView() {

    }

    @Override
    protected void onData() {
        // 检查蓝牙
        checkBluetooth();

        //
        searchBluetoothDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.unregisterBluetoothStateListener(bluetoothStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // 蓝牙已打开
            } else {
                // 蓝牙未打开
                ToastUtils.show(R.string.bluetooth_is_not_enabled);
            }
        }
    }

    /**
     * 扫描
     *
     * @param v 视图
     */
    @OnClick(R.id.bluetoothScan)
    public void onScanClick(View v) {
        CharSequence scanText = bluetoothScan.getText();
        if ("扫描".contentEquals(scanText)) {
            checkBluetooth();
            searchBluetoothDevices();
        } else if ("停止".contentEquals(scanText)) {
            stopSearchBluetoothDevices();
        }
    }

    private void checkBluetooth() {
        if (!mClient.isBleSupported()) {
            ToastUtils.show("设备不支持蓝牙");
        } else {
            if (!mClient.isBluetoothOpened()) {
                mClient.openBluetooth();
            }
            mClient.registerBluetoothStateListener(bluetoothStateListener);
        }
    }

    private void initSection() {
        pairedSection = QMUIGroupListView.newSection(this)
                .setTitle("已连接设备");
        searchedSection = QMUIGroupListView.newSection(this)
                .setTitle("已搜索设备");
    }

    private void searchBluetoothDevices() {
        mClient.search(searchRequest, searchResponse);
    }

    private void stopSearchBluetoothDevices() {
        mClient.stopSearch();
    }

    private void addListItemView(QMUIGroupListView.Section section, SearchResult searchResult, View.OnClickListener onClickListener) {
        Log.i(TAG, "蓝牙名称：" + searchResult.getName() + " 蓝牙地址：" + searchResult.getAddress());
        QMUICommonListItemView listItemView = blueToothGroupListView.createItemView(searchResult.getName());
        listItemView.setDetailText(searchResult.getAddress());
        listItemView.setOrientation(QMUICommonListItemView.VERTICAL);
        section.addItemView(listItemView, onClickListener);
    }

    private void addToListView(QMUIGroupListView.Section section) {
        section.removeFrom(blueToothGroupListView);
        section.addTo(blueToothGroupListView);
    }

    private BluetoothStateListener bluetoothStateListener = new BluetoothStateListener() {

        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
            Log.i(TAG, "蓝牙状态切换：" + openOrClosed);
        }
    };

    private View.OnClickListener searchedClickListener = view -> {
        // 连接状态
        if (view instanceof QMUICommonListItemView) {
            QMUICommonListItemView qmuiView = (QMUICommonListItemView) view;
            CharSequence address = qmuiView.getDetailText();
            Log.i(TAG, "选择蓝牙：" + address);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
}
