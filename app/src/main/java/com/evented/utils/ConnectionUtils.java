package com.evented.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Null-Pointer on 6/6/2015.
 */
@SuppressWarnings("unused")
public class ConnectionUtils {

    private static final String TAG = ConnectionUtils.class.getSimpleName();
    private static final AtomicBoolean isConnected = new AtomicBoolean(false);

    public static void init() {
        if (!initialised) {
            initialised = true;
            setUpConnectionState(null);
        }
    }

    private static volatile boolean initialised = false;

    static synchronized void setUpConnectionState(Intent intent) {
        if (intent == null) {
            NetworkInfo networkInfo = getNetworkInfo();
            isConnected.set(networkInfo != null && (networkInfo.isConnected() || networkInfo.isConnectedOrConnecting()));
        } else {
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
            if (networkInfo == null) {
                isConnected.set(!intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false));
            } else {
                isConnected.set(networkInfo.isConnected() || networkInfo.isConnectedOrConnecting());
            }
        }
        notifyListeners(isConnected.get());
    }

    public static boolean isConnected() {
        return isConnected.get();
    }

    public static boolean isConnectedOrConnecting() {
        NetworkInfo networkInfo = getNetworkInfo();
        return ((networkInfo != null) && networkInfo.isConnectedOrConnecting());
    }

    private static NetworkInfo getNetworkInfo() {
        Context context = Config.getApplicationContext();
        ConnectivityManager manager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return manager.getActiveNetworkInfo();
    }

    public static boolean isWifiConnected() {
        if (!isConnected()) {
            return false;
        }
        NetworkInfo info = getNetworkInfo();
        int type = info.getType();
        return type == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isMobileConnected() {
        if (!isConnected()) {
            return false;
        }
        NetworkInfo info = getNetworkInfo();
        int type = info.getType();
        return type == ConnectivityManager.TYPE_MOBILE;
    }

    public static boolean isActuallyConnected() {
        ThreadUtils.ensureNotMain();
        return isConnected() && isReallyReallyConnected();
    }

    private static boolean isReallyReallyConnected() {
        HttpURLConnection connection = null;
        InputStream in = null;
        //noinspection EmptyCatchBlock
        try {
            connection = (HttpURLConnection) new URL("https://facebook.com").openConnection();
            connection.connect();
            in = connection.getInputStream();
            if (in.read() != -1) {
                PLog.d(TAG, "user has real network connection");
                return true;
            }
        } catch (UnknownHostException | SocketTimeoutException e) {
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getCause());
        } catch (IOException e) {
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            IOUtils.closeQuietly(in);
        }
        PLog.d(TAG, "not connected");
        return false;
    }

    private static final List<WeakReference<ConnectivityListener>> listeners = new ArrayList<>();

    public static void registerConnectivityListener(ConnectivityListener listener) {
        ensureNotNull(listener);
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                if (listeners.get(i).get() == listener) {
                    PLog.d(TAG, "already registered");
                    return;
                }
            }
            listeners.add(new WeakReference<>(listener));
            listener.onConnectivityChanged(isConnected());
        }
    }

    public static void unRegisterConnectivityListener(ConnectivityListener listener) {
        ensureNotNull(listener);
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                WeakReference<ConnectivityListener> connectivityListenerWeakReference = listeners.get(i);
                if (connectivityListenerWeakReference.get() == listener) {
                    listeners.remove(connectivityListenerWeakReference);
                    return;
                }
            }
            PLog.d(TAG, "listener not found to be unregistered");
        }

    }

    private static void notifyListeners(boolean connected) {
        synchronized (listeners) {
            for (int i = 0; i < listeners.size(); i++) {
                ConnectivityListener weakReference = listeners.get(i).get();
                if (weakReference != null) {
                    weakReference.onConnectivityChanged(connected);
                }
            }
            //clean up dead weak references
            for (int i = 0; i < listeners.size(); i++) {
                WeakReference<ConnectivityListener> weakReference = listeners.get(i);
                if (weakReference.get() == null) {
                    listeners.remove(weakReference);
                }
            }
        }
    }

    public interface ConnectivityListener {
        void onConnectivityChanged(boolean connected);
    }

    static void ensureNotNull(Object object) {
        if (object == null) {
            throw new NullPointerException("null!");
        }
    }
}
