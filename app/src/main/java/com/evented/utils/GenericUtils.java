package com.evented.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.format.DateUtils;
import android.widget.EditText;

import com.evented.R;
import com.evented.events.ui.HomeScreenItemAdapter;

import java.io.File;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

/**
 * author Null-Pointer on 1/11/2016.
 */
public class GenericUtils {
    public static final NumberFormat FORMAT = DecimalFormat.getNumberInstance();

    static {
        GenericUtils.FORMAT.setMaximumFractionDigits(2);
        GenericUtils.FORMAT.setMinimumFractionDigits(2);
        GenericUtils.FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    }

    public static String format(double num) {
        return FORMAT.format(num);
    }

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

    public static boolean isEmpty(@NonNull EditText editText) {
        return editText.getText().length() == 0 || GenericUtils.isEmpty(editText.getText().toString().trim());
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

    public static void assertThat(boolean condition) {
        assertThat(condition, "assertion failed");
    }

    public static boolean hasPermission(FragmentActivity appCompatActivity, boolean askIfNotGranted, int requestCode, String... permissions) {
        assertThat(permissions.length > 0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        boolean hasPermission = true;
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(appCompatActivity, permission)) {
                hasPermission = false;
                break;
            }
        }
        if (!hasPermission && askIfNotGranted) {
            appCompatActivity.requestPermissions(permissions, requestCode);
        }
        return hasPermission;
    }

    public static boolean hasPermission(Fragment fragment, boolean askIfNotGranted, int requestCode, String... permissions) {
        assertThat(permissions.length > 0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        boolean hasPermission = true;
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(fragment.getContext(), permission)) {
                hasPermission = false;
                break;
            }
        }
        if (!hasPermission && askIfNotGranted) {
            fragment.requestPermissions(permissions, requestCode);
        }
        return hasPermission;
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

    public static void showDialog(Context context, String message) {
        new android.support.v7.app.AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

    public static Bitmap loadBitmap(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inJustDecodeBounds = false;
        double scalFactor = Math.ceil(Math.min(options.outWidth * 1.0 / width,
                options.outHeight * 1.0 / height * 1.0));
        options.inSampleSize = (int) scalFactor;
        if (options.inSampleSize < 1) {
            options.inSampleSize = 1;
        }
        return BitmapFactory.decodeFile(path, options);
    }

    /****************************************for testing****************************************/
    public static int getResIdentifier(int position) {
        switch (position) {
            case 0:
                return R.drawable.flyer;
            case 1:
                return R.drawable.flyer2;
            case 2:
                return R.drawable.flyer20;
            case 3:
                return R.drawable.flyer3;
            case 4:
                return R.drawable.flyer4;
            case 5:
                return R.drawable.flyer8;
            case 6:
                return R.drawable.flyer11;
            case 7:
                return R.drawable.flyer14;
            case 8:
                return R.drawable.flyer18;
            case 9:
                return R.drawable.flyer21;
            default:
                return R.drawable.flyer18;
        }
    }

    /*******************************************for testing*************************************/
    public static void setUpDrawables(Context context) {

        if (!HomeScreenItemAdapter.drawables.isEmpty()) {
            return;
        }
        for (int i = 0; i < 10; i++) {
            int res = getResIdentifier(i);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(),
                    res, options);
            final int requiredHeight = context.getResources().getDimensionPixelSize(R.dimen.home_screen_item_flyer_height);
            final int requiredWidth = context.getResources().getDimensionPixelSize(R.dimen.home_screen_item_width);

            options.inSampleSize = Math.max(options.outWidth / requiredWidth, options.outHeight / requiredHeight);
            if (options.inSampleSize == 0) {
                options.inSampleSize = 1;
            } else if (options.inSampleSize < 1) {
                options.inSampleSize = (int) Math.ceil(1 / options.inSampleSize);
            }
            options.inJustDecodeBounds = false;

            HomeScreenItemAdapter.drawables.add(new BitmapDrawable(BitmapFactory.decodeResource(context.getResources(),
                    res, options)));
        }
    }

    public static Bitmap getImage(Context context) {
        int res = getResIdentifier(Math.abs(new SecureRandom().nextInt()) % 10);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),
                res, options);
        final int requiredHeight = context.getResources().getDimensionPixelSize(R.dimen.highlights_height);
        final int requiredWidth = context.getResources().getDisplayMetrics().widthPixels;

        options.inSampleSize = Math.max(options.outWidth / requiredWidth, options.outHeight / requiredHeight);
        if (options.inSampleSize == 0) {
            options.inSampleSize = 1;
        } else if (options.inSampleSize < 1) {
            options.inSampleSize = (int) Math.ceil(1 / options.inSampleSize);
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(),
                res, options);
    }

    public static Bitmap vectorToDrawable(Context context, @DrawableRes int res) {
        Drawable carMarkerDrawable =
                ContextCompat.getDrawable(context,
                        res);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            carMarkerDrawable = DrawableCompat.wrap(carMarkerDrawable).mutate();
        }
        carMarkerDrawable.setBounds(0, 0, carMarkerDrawable.getIntrinsicWidth(), carMarkerDrawable.getIntrinsicHeight());

        Bitmap carMarker = Bitmap.createBitmap(carMarkerDrawable.getIntrinsicWidth(),
                carMarkerDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        carMarkerDrawable.draw(new Canvas(carMarker));
        return carMarker;
    }

    @SuppressLint("ApplySharedPref")
    public static void clearSharedPrefs(Context context) {
        File file = context.getDir("shared_prefs", Context.MODE_PRIVATE);
        File[] files = file.listFiles();
        for (File sharedPref : files) {
            context.getSharedPreferences(sharedPref.getName(), Context.MODE_PRIVATE)
                    .edit().clear().commit();
            org.apache.commons.io.FileUtils.deleteQuietly(sharedPref);
        }
    }

    public static void open(FragmentActivity activity, Intent intent) {
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            showDialog(activity, e.getMessage());
        }
    }
}
