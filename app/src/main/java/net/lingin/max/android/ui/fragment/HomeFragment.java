package net.lingin.max.android.ui.fragment;

import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import net.lingin.max.android.R;
import net.lingin.max.android.ui.base.BaseFragment;

import butterknife.BindView;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @Override
    protected int onLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void onView() {
        initTopBar();
    }

    private void initTopBar() {
        mTopBar.setTitle("首页");
    }
}
