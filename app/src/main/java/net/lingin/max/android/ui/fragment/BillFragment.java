package net.lingin.max.android.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.king.zxing.Intents;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import net.lingin.max.android.R;
import net.lingin.max.android.model.GoodsPrintDTO;
import net.lingin.max.android.ui.activity.QRCodeActivity;
import net.lingin.max.android.ui.adapter.GoodsLinearItemAdapter;
import net.lingin.max.android.ui.base.BaseFragment;
import net.lingin.max.android.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BillFragment extends BaseFragment {

    private static final String TAG = BillFragment.class.getName();

    /* 倒计时总时间(单位:秒) */
    private final static int TIME = 3;

    /* 倒计时一次(单位:秒) */
    private final static int SECONDS = 1;

    public static final int REQUEST_CODE = 1225;

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.rv_goods)
    RecyclerView goodsRecyclerView;

    private GoodsLinearItemAdapter goodsLinearItemAdapter;

    private Disposable disposable;

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

        initRecyclerView();
    }

    private void initData() {
        List<GoodsPrintDTO> goodsPrintDTOS = new ArrayList<>(10);
        disposable = Observable.interval(0, SECONDS, TimeUnit.SECONDS)
                .map(aLong -> TIME - aLong)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (aLong < 0) {
                        goodsLinearItemAdapter.setData(goodsPrintDTOS);
                    } else {
                        GoodsPrintDTO goodsPrintDTO = new GoodsPrintDTO();
                        goodsPrintDTO.setItemNo("010017");
                        goodsPrintDTO.setItemName("达利园2.5kg软面包散装（香橙）");
                        goodsPrintDTO.setItemSize("1*2.5");
                        goodsPrintDTO.setSalePrice(25.0000);
                        goodsPrintDTO.setUnitNo("公斤");
                        goodsPrintDTOS.add(goodsPrintDTO);
                    }
                });
    }

    @OnClick(R.id.sweepCode)
    public void onSweepCodeClick(View view) {
        // 跳转到扫码界面
        startActivityForResult(new Intent(getActivity(), QRCodeActivity.class), REQUEST_CODE);
    }

    @OnClick(R.id.goodsPrint)
    public void onGoodsPrintClick(View view) {
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
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
                initData();
            }
        }
    }

    private void initTopBar() {
        mTopBar.setTitle("票据");
    }

    private void initRecyclerView() {
        VirtualLayoutManager connectedManager = new VirtualLayoutManager(getActivity());
        LayoutHelper goodsHelper = new LinearLayoutHelper();
        goodsRecyclerView.setLayoutManager(connectedManager);
        // 分割线
        goodsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        goodsLinearItemAdapter = new GoodsLinearItemAdapter(getActivity(), goodsHelper);
        goodsRecyclerView.setAdapter(goodsLinearItemAdapter);
    }
}
