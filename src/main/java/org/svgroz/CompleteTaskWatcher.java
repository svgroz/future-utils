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

    private final Long splitBySize;

    public CompleteTaskWatcher(
            final Long splitBySize
    ) {
        Objects.requireNonNull(splitBySize, "splitBySize should be not null");
        this.splitBySize = splitBySize;
    }

    @Override
    public void addTasks(
            final Collection<WatcherTask<T>> tasks,
            final ExecutorService executorService
    ) {
        Objects.requireNonNull(tasks, "tasks should be not null");
        Objects.requireNonNull(executorService, "executorService should be not null");
        if (tasks.isEmpty()) {
            return;
        }
        makeSubTasks(tasks, executorService);
    }

    private void makeSubTasks(
            final Collection<WatcherTask<T>> tasks,
            final ExecutorService watchOn
    ) {
        ArrayList<WatcherTask<T>> ts = new ArrayList<>();
        for (WatcherTask<T> task : tasks) {
            if (ts.size() == splitBySize) {
                watchOn.submit(
                        new CompleteTaskWatcherSubTask<>(
                                task.id,
                                watchOn,
                                ts
                        )
                );
                ts = new ArrayList<>();
            }

            ts.add(task);
        }

        watchOn.submit(
                new CompleteTaskWatcherSubTask<>(
                        UUID.randomUUID(),
                        watchOn,
                        ts
                )
        );
    }
}
