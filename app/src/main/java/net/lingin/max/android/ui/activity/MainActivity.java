package net.lingin.max.android.ui.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.roughike.bottombar.BottomBar;

import net.lingin.max.android.R;
import net.lingin.max.android.logger.Log;
import net.lingin.max.android.ui.adapter.ContentContainerFragmentPagerAdapter;
import net.lingin.max.android.ui.base.BaseActivity;
import net.lingin.max.android.ui.fragment.BillFragment;
import net.lingin.max.android.ui.fragment.HomeFragment;
import net.lingin.max.android.ui.fragment.InstallFragment;
import net.lingin.max.android.ui.fragment.MeFragment;
import net.lingin.max.android.ui.listener.ContentContainerOnPageChangeListener;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.contentContainer)
    ViewPager contentContainer;

    @BindView(R.id.bottomBar)
    BottomBar bottomBar;

    private List<Fragment> fragments = Arrays.asList(new HomeFragment(), new BillFragment(), new InstallFragment(), new MeFragment());

    @Override
    protected int onLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onObject() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        contentContainer.setAdapter(new ContentContainerFragmentPagerAdapter(fragmentManager, fragments));
        contentContainer.addOnPageChangeListener(new ContentContainerOnPageChangeListener());
    }

    @Override
    protected void onView() {
        bottomBar.setOnTabSelectListener(tabId -> {
            switch (tabId) {
                case R.id.tab_home:
                    contentContainer.setCurrentItem(0);
                    break;
                case R.id.tab_bill:
                    contentContainer.setCurrentItem(1);
                    break;
                case R.id.tab_install:
                    contentContainer.setCurrentItem(2);
                    break;
                case R.id.tab_me:
                    contentContainer.setCurrentItem(3);
                    break;
                default:
                    break;
            }
            Log.i("选中项：" + tabId);
        });
    }

    @Override
    protected void onData() {

    }
}
