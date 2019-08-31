package org.svgroz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class FutureWatcherTask<T> extends WatcherTask<Future<T>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FutureWatcherTask.class);

    private final CompletableFuture<T> onComplete;

    public FutureWatcherTask(
            final Object id,
            final Future<T> watchedOn,
            final CompletableFuture<T> onComplete
    ) {
        super(id, watchedOn);
        Objects.requireNonNull(onComplete, "onComplete should be not null");
        this.onComplete = onComplete;
    }

    @Override
    public void accept(DeleteCallback deleteCallback) {
        if (watchedOn.isDone()) {
            try {
                onComplete.complete(watchedOn.get());
                LOGGER.debug("Task with id={} completed", id);
            } catch (Exception e) {
                LOGGER.warn("Task with id={} throw exception on get and removed from watched queue", id);
                onComplete.completeExceptionally(e);
            } finally {
                deleteCallback.delete();
            }
        } else if (watchedOn.isCancelled()) {
            deleteCallback.delete();
            LOGGER.debug("Task with id={} canceled", id);
        }
    }
}
