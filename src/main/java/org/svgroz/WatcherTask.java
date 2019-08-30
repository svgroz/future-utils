package org.svgroz;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class WatcherTask<T> {
    private final T watchedOn;
    private final Function<T, TaskState> checkStateFunction;
    private final Consumer<T> consumer;

    public WatcherTask(T watchedOn, Function<T, TaskState> checkStateFunction, Consumer<T> consumer) {
        Objects.requireNonNull(watchedOn, "watchedOn should be not null");
        Objects.requireNonNull(checkStateFunction, "checkStateFunction should be not null");
        Objects.requireNonNull(consumer, "consumer checkStateFunction");

        this.watchedOn = watchedOn;
        this.checkStateFunction = checkStateFunction;
        this.consumer = consumer;
    }

    public T getWatchedOn() {
        return watchedOn;
    }

    public Function<T, TaskState> getCheckStateFunction() {
        return checkStateFunction;
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    @Override
    public String toString() {
        return "CompleteWatcherTask{" +
                "watchedOn=" + watchedOn +
                ", checkStateFunction=" + checkStateFunction +
                ", consumer=" + consumer +
                '}';
    }
}
