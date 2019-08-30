package org.svgroz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

public class CompleteTaskWatcherSubTask<T> implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompleteTaskWatcherSubTask.class);

    private final Object id;
    private final ExecutorService executorService;
    private final Collection<WatcherTask<T>> tasks;

    public CompleteTaskWatcherSubTask(
            Object id,
            ExecutorService executorService,
            Collection<WatcherTask<T>> tasks
    ) {
        Objects.requireNonNull(id, "id should be not null");
        Objects.requireNonNull(executorService, "executorService should be not null");
        Objects.requireNonNull(tasks, "tasks should be not null");
        this.id = id;
        this.executorService = executorService;
        this.tasks = tasks;
    }

    @Override
    public void run() {
        final Iterator<WatcherTask<T>> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            final WatcherTask<T> next = iterator.next();
            final T watchedOn = next.getWatchedOn();
            final BiConsumer<T, DeleteCallback<T>> consumer = next.getConsumer();
            consumer.accept(watchedOn, (watchedOnX) -> iterator.remove());
        }

        if (!tasks.isEmpty()) {
            executorService.submit(this);
        }
    }
}
