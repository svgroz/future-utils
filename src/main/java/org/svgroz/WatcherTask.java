package org.svgroz;

import java.util.Objects;
import java.util.function.BiConsumer;

public class WatcherTask<T> {
    private final T watchedOn;
    private final BiConsumer<T, DeleteCallback<T>> consumer;

    public WatcherTask(T watchedOn, BiConsumer<T, DeleteCallback<T>> consumer) {
        Objects.requireNonNull(watchedOn, "watchedOn should be not null");
        Objects.requireNonNull(consumer, "consumer checkStateFunction");

        this.watchedOn = watchedOn;
        this.consumer = consumer;
    }

    public T getWatchedOn() {
        return watchedOn;
    }

    public BiConsumer<T, DeleteCallback<T>> getConsumer() {
        return consumer;
    }

    @Override
    public String toString() {
        return "CompleteWatcherTask{" +
                "watchedOn=" + watchedOn +
                ", consumer=" + consumer +
                '}';
    }
}
