package com.evented.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by yaaminu on 8/10/17.
 */

public class Behavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private int mDySinceDirectionChange;
    private AccelerateDecelerateInterpolator interpolator;

    public Behavior() {
        super();
        interpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dx, int dy, int[] consumed) {
        if (dy > 0 && mDySinceDirectionChange < 0
                || dy < 0 && mDySinceDirectionChange > 0) {
            mDySinceDirectionChange = 0;
        }

        mDySinceDirectionChange += dy;

        if (mDySinceDirectionChange > child.getHeight()
                && child.getVisibility() == View.VISIBLE) {
            hide(child);
        } else if (mDySinceDirectionChange < 0
                && child.getVisibility() == View.GONE) {
            show(child);
        }
    }

    private void hide(final View view) {
        view.animate()
                .translationY(view.getHeight())
                .setInterpolator(interpolator)
                .setDuration(200)
                .start();
    }

    private void show(final View view) {
        view.animate()
                .translationY(0)
                .setInterpolator(interpolator)
                .setDuration(200)
                .start();
    }
}
