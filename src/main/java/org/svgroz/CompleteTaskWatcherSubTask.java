package org.svgroz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

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
            final Function<T, TaskState> checkStateFunction = next.getCheckStateFunction();
            final TaskState taskState = checkStateFunction.apply(watchedOn);
            switch (taskState) {
                case COMPLETED: {
                    final Consumer<T> consumer = next.getConsumer();
                    consumer.accept(watchedOn);
                    iterator.remove();
                    break;
                }
                case CANCELED:
                case OUT_OF_TIME: {
                    iterator.remove();
                    break;
                }
                case IN_PROCESS: {
                    continue;
                }
                default:
                    throw new UnsupportedOperationException("WatcherTask.getCheckStateFunction return unsupported taskSate: " + taskState);
            }
        }

        if (!tasks.isEmpty()) {
            executorService.submit(this);
        }
    }
}
