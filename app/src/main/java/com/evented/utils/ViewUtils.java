package com.evented.utils;

import android.view.View;

/**
 * @author Null-Pointer on 9/20/2015.
 */
public class ViewUtils {

    public static boolean isViewVisible(View view) {
        return view != null && view.getVisibility() == View.VISIBLE;
    }

    private static void hideViewInternal(View view) {
        if (isViewVisible(view)) {
            view.setVisibility(View.GONE);
        }
    }

    private static void showViewInternal(View view) {
        if (!isViewVisible(view) && view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideViews(View... views) {
        for (View view : views) {
            hideViewInternal(view);
        }
    }

    public static void showViews(View... views) {
        for (View view : views) {
            showViewInternal(view);
        }
    }

    public static void showByFlag(boolean flag, View... view) {
        if (flag) {
            showViews(view);
        } else {
            hideViews(view);
        }
    }


    public static void toggleVisibility(View view, boolean flag) {
        if (view != null) {
            view.setVisibility(flag ? View.VISIBLE : View.GONE);
        }
    }
}
