package net.lingin.max.android.ui.activity;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.widget.tab.QMUITabSegment;

import net.lingin.max.android.R;

import java.util.HashMap;

import butterknife.BindView;

/**
 * @author LISER
 * @date 2019/12/24 1:44
 */
public class QMainactivity extends QMUIActivity {

    @BindView(R.id.pager)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    QMUITabSegment mTabSegment;

    private HashMap<Pager, Fragment> mPages;

    private void initTabs() {


    }

    enum Pager {

        COMPONENT, UTIL, LAB;

        public static Pager getPagerFromPositon(int position) {
            switch (position) {
                case 0:
                    return COMPONENT;
                case 1:
                    return UTIL;
                case 2:
                    return LAB;
                default:
                    return COMPONENT;
            }
        }
    }
}
