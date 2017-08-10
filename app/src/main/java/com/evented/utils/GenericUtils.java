package com.evented.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.StringRes;
import android.text.format.DateUtils;

import java.util.Calendar;

/**
 * author Null-Pointer on 1/11/2016.
 */
public class GenericUtils {
    private GenericUtils() {
    }

    public static void ensureNotNull(Object... o) {
        if (o == null) throw new IllegalArgumentException("null");
        for (int i = 0; i < o.length; i++) {
            if (o[i] == null) {
                throw new IllegalArgumentException("null");
            }
        }
    }

    public static void ensureNotNull(Object o, String message) {
        ensureNotNull(message);
        if (o == null) throw new IllegalArgumentException(message);
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.toString().trim().length() == 0;
    }

    public static String getString(@StringRes int res) {
        return Config.getApplicationContext().getString(res);
    }

    public static String getString(@StringRes int res, Object... args) {
        return Config.getApplicationContext().getString(res, args);
    }

    public static void ensureNotEmpty(String... args) {
        if (args == null) throw new IllegalArgumentException("null");
        for (int i = 0; i < args.length; i++) {
            if (isEmpty(args[i])) {
                throw new IllegalArgumentException("null");
            }
        }
    }

    public static void ensureConditionTrue(boolean condition, String message) {
        message = message == null ? "" : message;
        if (!condition)
            throw new IllegalArgumentException(message);
    }

    public static boolean isCapitalised(String text) {
        ensureNotNull(text);
        return text.equals(capitalise(text));
    }

    public static String capitalise(String text) {
        text = text != null ? text : "";
        if (isEmpty(text.trim())) return text;
        StringBuilder builder = new StringBuilder(text);
        boolean previousWasSpace = true; //capitalize sentence
        //we assume that the string is trimmed
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if (previousWasSpace) {//don't check whether char is letter or not
                builder.setCharAt(i, Character.toUpperCase(c));
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
            previousWasSpace = Character.isSpaceChar(c);
        }
        return builder.toString();
    }

    public static void assertThat(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    public static boolean hasPermission(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        for (String permission : permissions) {
            if (context.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean wasPermissionGranted(String[] permissions, int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (activity.shouldShowRequestPermissionRationale(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String formatDateTime(Context context, Calendar today, Calendar date) {
        int flags = 0;
        flags |= (DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR);
        if (today.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
            if (today.get(Calendar.WEEK_OF_YEAR) == date.get(Calendar.WEEK_OF_YEAR)) {
                if (today.get(Calendar.DAY_OF_WEEK) != date.get(Calendar.DAY_OF_WEEK)) {
                    flags |= DateUtils.FORMAT_SHOW_WEEKDAY;
                }
            } else {
                flags |= DateUtils.FORMAT_SHOW_DATE;
            }
        } else {
            flags |= (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
        }
        return DateUtils.formatDateTime(context, date.getTimeInMillis(), flags);
    }

}
