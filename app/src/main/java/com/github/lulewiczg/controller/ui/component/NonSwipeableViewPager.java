package com.github.lulewiczg.controller.ui.component;

import android.app.Activity;
import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.lulewiczg.controller.common.Helper;

/**
 * Created by Grzegurz on 2016-04-02.
 */

public class NonSwipeableViewPager extends ViewPager implements ViewPager.OnPageChangeListener {

    private Activity activity;

    public NonSwipeableViewPager(Context context) {
        super(context);
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }


    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onPageScrolled(int pos, float offset, int offsetPx) {
        super.onPageScrolled(pos, offset, offsetPx);
        //Android, much designed API, wow
        if (activity != null) {
            Helper.hideKeyboard(activity);
        }
    }

    @Override
    public void onPageSelected(int i) {
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }
}
