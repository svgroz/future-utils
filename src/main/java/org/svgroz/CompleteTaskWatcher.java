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


    public void makeSubTasks(
            final Collection<WatcherTask<T>> tasks,
            final ExecutorService watchOn
    ) {
        if (tasks.isEmpty()) {
            return;
        }

        ArrayList<WatcherTask<T>> ts = new ArrayList<>();
        for (WatcherTask<T> task : tasks) {
            if (ts.size() == splitBySize) {
                watchOn.submit(
                        new CompleteTaskWatcherSubTask<>(
                                UUID.randomUUID(),
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
