package com.android.launcher3;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 *
 *  竖屏模式下根布局，继承了InsettableFrameLayout，控制是否显示在状态栏等下面
 *
 */
public class LauncherRootView extends InsettableFrameLayout {
    public LauncherRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        setInsets(insets);
        return true; // I'll take it from here
    }
}