package net.lingin.max.android.ui.listener;

import androidx.viewpager.widget.ViewPager;

import net.lingin.max.android.logger.Log;

public class ContentContainerOnPageChangeListener implements ViewPager.OnPageChangeListener {

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.i("翻页：" + position + " " + positionOffset + " " + positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        Log.i("已选定页面：" + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.i("页面滚动状态已更改：" + state);
    }
}
