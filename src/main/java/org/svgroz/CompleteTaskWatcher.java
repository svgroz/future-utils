package org.svgroz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class CompleteTaskWatcher<T> implements TaskWatcher<WatcherTask<T>>, Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompleteTaskWatcher.class);

    private volatile boolean isTerminated = false;

    private final Long splitBySize;
    private final ExecutorService executorService;
    private final Collection<WatcherTask<T>> tasks;

    private CompleteTaskWatcher(
            final Long splitBySize,
            final ExecutorService executorService,
            final Collection<WatcherTask<T>> tasks
    ) {
        Objects.requireNonNull(splitBySize, "splitBySize should be not null");
        Objects.requireNonNull(executorService, "executorService should be not null");
        Objects.requireNonNull(tasks, "tasks should be not null");
        this.splitBySize = splitBySize;
        this.executorService = executorService;
        this.tasks = tasks;
    }

    @Override
    public void addTask(final WatcherTask<T> task) {
        tasks.add(task);
    }

    @Override
    public void run() {
        if (isTerminated) {
            return;
        }

        makeSubTasks();

        executorService.submit(this);
    }

    private void makeSubTasks() {
        if (tasks.isEmpty()) {
            return;
        }

        ArrayList<WatcherTask<T>> ts = new ArrayList<>();
        for (WatcherTask<T> task : tasks) {
            if (ts.size() == splitBySize) {
                executorService.submit(
                        new CompleteTaskWatcherSubTask<>(
                                UUID.randomUUID(),
                                executorService,
                                ts
                        )
                );
                ts = new ArrayList<>();
            }

            ts.add(task);
        }

        if (ts.isEmpty()) {
            return;
        }

        executorService.submit(
                new CompleteTaskWatcherSubTask<>(
                        UUID.randomUUID(),
                        executorService,
                        ts
                )
        );
    }

    public void terminate() {
        this.isTerminated = true;
    }
}
