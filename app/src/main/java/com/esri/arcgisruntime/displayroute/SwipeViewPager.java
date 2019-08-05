package com.esri.arcgisruntime.displayroute;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.view.MotionEvent;

public class SwipeViewPager extends ViewPager {

    boolean swipe = false;

    public SwipeViewPager(Context context) {
        super(context);
    }

    public SwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return swipe && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        return swipe && super.onTouchEvent(ev);

    }

    public void swipe(boolean swipe) {
        this.swipe = swipe;
    }
}
