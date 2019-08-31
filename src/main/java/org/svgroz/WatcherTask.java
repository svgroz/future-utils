package org.svgroz;

import java.util.Objects;

public abstract class WatcherTask<T> {
    final Object id;
    final T watchedOn;

    public WatcherTask(
            final Object id,
            final T watchedOn
    ) {
        Objects.requireNonNull(id, "id should be not null");
        Objects.requireNonNull(watchedOn, "watchedOn should be not null");

        this.id = id;
        this.watchedOn = watchedOn;
    }

    public T getWatchedOn() {
        return watchedOn;
    }

    public abstract void accept(final DeleteCallback deleteCallback);

    @Override
    public String toString() {
        return "WatcherTask{" +
                "id=" + id +
                ", watchedOn=" + watchedOn +
                '}';
    }
}
