package org.svgroz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class CompleteTaskWatcher<T> implements TaskWatcher<WatcherTask<T>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompleteTaskWatcher.class);

    private volatile boolean isTerminated = false;

    private final Long splitBySize;
    private final ExecutorService executorService;

    public CompleteTaskWatcher(
            final Long splitBySize,
            final ExecutorService executorService
    ) {
        Objects.requireNonNull(splitBySize, "splitBySize should be not null");
        Objects.requireNonNull(executorService, "executorService should be not null");
        this.splitBySize = splitBySize;
        this.executorService = executorService;
    }


    public void makeSubTasks(Collection<WatcherTask<T>> tasks) {
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

        executorService.submit(
                new CompleteTaskWatcherSubTask<>(
                        UUID.randomUUID(),
                        executorService,
                        ts
                )
        );
    }
}
