package net.lingin.max.android.ui.activity;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.tab.QMUITab;
import com.qmuiteam.qmui.widget.tab.QMUITabBuilder;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;

import net.lingin.max.android.R;
import net.lingin.max.android.ui.adapter.TabsFragmentPagerAdapter;
import net.lingin.max.android.ui.base.BaseActivity;
import net.lingin.max.android.ui.fragment.BillFragment;
import net.lingin.max.android.ui.fragment.InstallFragment;
import net.lingin.max.android.ui.fragment.MeFragment;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.contentContainer)
    ViewPager contentContainer;

    @BindView(R.id.tabs)
    QMUITabSegment mTabSegment;

    private List<Fragment> fragments = Arrays.asList(new BillFragment(), new InstallFragment(), new MeFragment());

    @Override
    protected int onLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onObject() {

    }

    @Override
    protected void onView() {
        initTabs();
        initTabsWithPages();
    }

    @Override
    protected void onData() {

    }

    private void initTabs() {
        QMUITabBuilder builder = mTabSegment.tabBuilder();
        builder.setSelectedIconScale(1.2f)
                .setTextSize(QMUIDisplayHelper.sp2px(this, 13), QMUIDisplayHelper.sp2px(this, 15))
                .setDynamicChangeIconColor(false);
        QMUITab component = builder
                .setNormalDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_tabbar_component))
                .setSelectedDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_tabbar_component_selected))
                .setText("票据")
                .build(this);
        QMUITab util = builder
                .setNormalDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_tabbar_util))
                .setSelectedDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_tabbar_util_selected))
                .setText("设置")
                .build(this);
        QMUITab lab = builder
                .setNormalDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_tabbar_lab))
                .setSelectedDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_tabbar_lab_selected))
                .setText("我的")
                .build(this);
        mTabSegment
                .addTab(component)
                .addTab(util)
                .addTab(lab);
    }

    private void initTabsWithPages() {
        mTabSegment.addOnTabSelectedListener(new QMUITabSegment.OnTabSelectedListener() {

            @Override
            public void onTabSelected(int index) {
                contentContainer.setCurrentItem(index);
            }

            @Override
            public void onTabUnselected(int index) {

            }

            @Override
            public void onTabReselected(int index) {

            }

            @Override
            public void onDoubleTap(int index) {

            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        TabsFragmentPagerAdapter pagerAdapter = new TabsFragmentPagerAdapter(fragmentManager, fragments);
        contentContainer.setAdapter(pagerAdapter);
        mTabSegment.setupWithViewPager(contentContainer, false);
    }
}
