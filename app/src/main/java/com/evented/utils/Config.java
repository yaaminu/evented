package com.evented.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.evented.BuildConfig;
import com.evented.R;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author null-pointer
 */
@SuppressWarnings("unused")
public class Config {

    public static final String APP_PREFS = "prefs";
    private static final String TAG = Config.class.getSimpleName();

    private static final String logMessage = "calling getApplication when init has not be called";
    private static final String detailMessage = "application is null. Did you forget to call Config.init()?";
    private static int APP_VERSION_CODE;
    private static String APP_NAME;
    private static Application application;
    private static AtomicBoolean isAppOpen = new AtomicBoolean(false);
    private static boolean debug;
    private static boolean isManagement;

    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static int getAppVersionCode() {
        ensureInitialised();
        return APP_VERSION_CODE;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void init(Application pairApp, String appName, int appVersionCode, boolean debug) {
        Config.application = pairApp;
        Config.debug = debug;
        Config.APP_VERSION_CODE = appVersionCode;
        Config.APP_NAME = appName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                setUpDirs();
            }
        });
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private synchronized static void setUpDirs() {
        if (isExternalStorageAvailable()) {
            //no need to worry calling this several times
            //if the file is already a directory it will fail silently
            getAppBinFilesBaseDir().mkdirs();
            getTempDir().mkdirs();
        } else {
            PLog.w(TAG, "This is strange! no sdCard available on this device");
        }

    }

    public static boolean isAppOpen() {
        return isAppOpen.get();
    }

    public static void appOpen(boolean appOpen) {
        isAppOpen.set(appOpen);
    }

    public static Context getApplicationContext() {
        if (application == null) {
            warnAndThrow(logMessage, detailMessage);
        }
        return application.getApplicationContext();
    }

    public static Application getApplication() {
        if (application == null) {
            warnAndThrow(logMessage, detailMessage);
        }
        return Config.application;
    }

    private static void warnAndThrow(String msg, String detailMessage) {
        PLog.w(TAG, msg);
        throw new IllegalStateException(detailMessage);
    }


    public static SharedPreferences getApplicationWidePrefs() {
        ensureInitialised();
        return getPreferences(Config.APP_PREFS);
    }

    private static void ensureInitialised() {
        if (application == null) {
            throw new IllegalStateException("application is null,did you forget to call init(Context) ?");
        }
    }

    public static File getAppBinFilesBaseDir() {
        File file = new File(Environment
                .getExternalStoragePublicDirectory(APP_NAME), getApplicationContext().getString(R.string.folder_name_files));
        if (!file.isDirectory()) {
            if (!file.mkdirs()) {
                PLog.f(TAG, "failed to create files dir");
            }
        }
        return file;
    }


    public static File getTempDir() {
        File file = new File(Environment
                .getExternalStoragePublicDirectory(APP_NAME), "TMP");
        if (!file.isDirectory()) {
            if (!file.mkdirs()) {
                PLog.f(TAG, "failed to create tmp dir");
            }
        }
        return file;
    }


    public static SharedPreferences getPreferences(String s) {
        return getApplicationContext().getSharedPreferences(s, Context.MODE_PRIVATE);
    }

    public static boolean isManagement() {
        return BuildConfig.DEBUG && isManagement;
    }

    public static void setManagement(boolean isManagement) {
        Config.isManagement = isManagement;
    }

}
