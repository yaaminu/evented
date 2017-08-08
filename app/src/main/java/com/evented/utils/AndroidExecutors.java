package com.evented.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class AndroidExecutors {

    /* package */ static final long KEEP_ALIVE_TIME = 1L;
    private static final AndroidExecutors INSTANCE = new AndroidExecutors();
    /**
     * Nexus 5: Quad-Core
     * Moto X: Dual-Core
     * <p/>
     * AsyncTask:
     * CORE_POOL_SIZE = CPU_COUNT + 1
     * MAX_POOL_SIZE = CPU_COUNT * 2 + 1
     * <p/>
     * https://github.com/android/platform_frameworks_base/commit/719c44e03b97e850a46136ba336d729f5fbd1f47
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /* package */ static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    /* package */ static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private final Executor uiThread, WORKER;

    private AndroidExecutors() {
        uiThread = new UIThreadExecutor();
        WORKER = new ThreadPoolExecutor(CPU_COUNT + 1, CPU_COUNT * 2 + 1, 1000L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128));
    }

    /**
     * Creates a proper Cached Thread Pool. Tasks will reuse cached threads if available
     * or create new threads until the core pool is full. tasks will then be queued. If an
     * task cannot be queued, a new thread will be created unless this would exceed max pool
     * size, then the task will be rejected. Threads will time out after 1 second.
     * <p/>
     * Core thread timeout is only available on android-9+.
     *
     * @return the newly created thread pool
     */
    public static ExecutorService newCachedThreadPool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());

        allowCoreThreadTimeout(executor, true);

        return executor;
    }

    /**
     * Creates a proper Cached Thread Pool. Tasks will reuse cached threads if available
     * or create new threads until the core pool is full. tasks will then be queued. If an
     * task cannot be queued, a new thread will be created unless this would exceed max pool
     * size, then the task will be rejected. Threads will time out after 1 second.
     * <p/>
     * Core thread timeout is only available on android-9+.
     *
     * @param threadFactory the factory to use when creating new threads
     * @return the newly created thread pool
     */
    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);

        allowCoreThreadTimeout(executor, true);

        return executor;
    }

    /**
     * Compatibility helper function for
     * {@link java.util.concurrent.ThreadPoolExecutor#allowCoreThreadTimeOut(boolean)}
     * <p/>
     * Only available on android-9+.
     *
     * @param executor the {@link java.util.concurrent.ThreadPoolExecutor}
     * @param value    true if should time out, else false
     */
    @SuppressLint("NewApi")
    public static void allowCoreThreadTimeout(ThreadPoolExecutor executor, boolean value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            executor.allowCoreThreadTimeOut(value);
        }
    }

    /**
     * An {@link java.util.concurrent.Executor} that executes tasks on the UI thread.
     */
    public static Executor uiThread() {
        return INSTANCE.uiThread;
    }

    static Executor threadPool() {
        return INSTANCE.WORKER;
    }

    /**
     * An {@link java.util.concurrent.Executor} that runs tasks on the UI thread.
     */
    private static class UIThreadExecutor implements Executor {
        @Override
        public void execute(@NonNull Runnable command) {
            new Handler(Looper.getMainLooper()).post(command);
        }
    }

}
