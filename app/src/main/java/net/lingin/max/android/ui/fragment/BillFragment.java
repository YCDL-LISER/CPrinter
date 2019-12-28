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
import net.lingin.max.android.business.GprinterService;
import net.lingin.max.android.net.Network;
import net.lingin.max.android.net.NetworkSubs;
import net.lingin.max.android.net.exception.ExceptionAction;
import net.lingin.max.android.net.model.GoodsPrintResult;
import net.lingin.max.android.ui.activity.QRCodeActivity;
import net.lingin.max.android.ui.adapter.GoodsLinearItemAdapter;
import net.lingin.max.android.ui.base.BaseFragment;
import net.lingin.max.android.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BillFragment extends BaseFragment {

    private static final String TAG = BillFragment.class.getName();

    private static final int REQUEST_CODE = 1225;

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.rv_goods)
    RecyclerView goodsRecyclerView;

    private GoodsLinearItemAdapter goodsLinearItemAdapter;

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

    @OnClick(R.id.sweepCode)
    public void onSweepCodeClick(View view) {
        // 跳转到扫码界面
        startActivityForResult(new Intent(getActivity(), QRCodeActivity.class), REQUEST_CODE);
    }

    @OnClick(R.id.goodsPrint)
    public void onGoodsPrintClick(View view) {
        if (goodsLinearItemAdapter.getData().size() > 0) {
            GprinterService.getInstance()
                    .printBill(goodsLinearItemAdapter.getData())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Integer integer) {
                            ToastUtils.show("打印成功条数：" + integer);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

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
                netWork();
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

    private void netWork() {
        Network.api()
                .goodsPrint("010017", "0023")
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new ExceptionAction())
                .subscribe(new NetworkSubs<GoodsPrintResult>() {
                    @Override
                    protected void onSuccess(GoodsPrintResult data) {
                        goodsLinearItemAdapter.addData(data);
                    }
                });
    }
}
