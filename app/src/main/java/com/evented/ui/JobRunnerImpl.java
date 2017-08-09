package com.evented.ui;

import com.birbit.android.jobqueue.CancelResult;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.TagConstraint;
import com.evented.utils.Task;
import com.evented.utils.TaskManager;

/**
 * Created by yaaminu on 8/9/17.
 */

public class JobRunnerImpl implements TaskManager.JobRunner {

    private final JobManager jobManager;

    public JobRunnerImpl(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public void runJob(Task task) {
        jobManager.addJobInBackground(task);
    }

    @Override
    public CancelResult cancelJobs(String tag) {
        return jobManager.cancelJobs(TagConstraint.ALL, tag);
    }

    @Override
    public void start() {
        jobManager.start();
    }
}
