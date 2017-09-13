package com.evented.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.birbit.android.jobqueue.CancelResult;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author by Null-Pointer on 9/27/2015.
 */
@SuppressWarnings("unused")
public class TaskManager {

    public static final String TAG = TaskManager.class.getSimpleName();
    private static final AtomicBoolean initialised = new AtomicBoolean(false);

    private static ExecutorService cachedThreadPool =
            new ThreadPoolExecutor(16, 64,
                    60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(512)),
            networkCachedThreadPool =
                    new ThreadPoolExecutor(16, 64,
                            60L, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(256));

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 3;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 4;
    private static final int KEEP_ALIVE = 60;

    private static final BlockingQueue<Runnable> nonNetworkWorkQueue =
            new LinkedBlockingQueue<>(256), networkWorkQueue = new LinkedBlockingQueue<>(512);

    public static final ExecutorService NON_NETWORK_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, nonNetworkWorkQueue),
            NETWORK_EXECUTOR
                    = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                    TimeUnit.SECONDS, networkWorkQueue);

    private static WeakReference<JobRunner> jobManager;


    public static void init(@NonNull JobRunner runner) {
        if (!initialised.getAndSet(true)) {
            PLog.w(TAG, "initialising %s", TAG);
            jobManager = new WeakReference<>(runner);
            runner.start();
        }
    }

    public static void runJob(Task job) {
        ThreadUtils.ensureNotMain();
        if (job == null || !job.isValid()) {
            throw new IllegalArgumentException("invalid job");
        }
        JobRunner jobRunner = jobManager.get();
        if (jobRunner == null) {
            throw new IllegalStateException();
        }
        jobRunner.runJob(job);
    }

    public static void executeOnMainThread(Runnable r) {
        new Handler(Looper.getMainLooper()).post(r);
    }

    @NonNull
    public static <T> Future<T> execute(Callable<T> task, boolean requiresNetwork) {
        if (requiresNetwork) {
            return NETWORK_EXECUTOR.submit(task);
        } else {
            return NON_NETWORK_EXECUTOR.submit(task);
        }
    }

    public static void execute(Runnable runnable, boolean requiresNetwork) {
        if (requiresNetwork) {
            NETWORK_EXECUTOR.execute(runnable);
        } else {
            NON_NETWORK_EXECUTOR.execute(runnable);
        }
    }


    public static void executeNow(Runnable runnable, boolean requiresNetwork) {
        if (requiresNetwork) {
            networkCachedThreadPool.execute(runnable);
        } else {
            cachedThreadPool.execute(runnable);
        }
    }

    public static Future<?> executeNow(Callable<?> callable, boolean requiresNetwork) {
        if (requiresNetwork) {
            return networkCachedThreadPool.submit(callable);
        } else {
            return cachedThreadPool.submit(callable);
        }
    }


    public static CancelResult cancelJobSync(String tag) {
        JobRunner jobRunner = jobManager.get();
        if (jobRunner == null) {
            throw new IllegalStateException();
        }
        return jobRunner.cancelJobs(tag);
    }

    public interface JobRunner {
        void runJob(Task task);

        CancelResult cancelJobs(String tag);

        void start();
    }
}
