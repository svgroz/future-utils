package org.svgroz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class CompleteTaskWatcherSubTask<T> implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompleteTaskWatcherSubTask.class);

    private final Object id;
    private final ExecutorService executorService;
    private volatile Collection<WatcherTask<T>> tasks;

    CompleteTaskWatcherSubTask(
            final Object id,
            final ExecutorService executorService,
            final Collection<WatcherTask<T>> tasks
    ) {
        Objects.requireNonNull(id, "id should be not null");
        Objects.requireNonNull(executorService, "executorService should be not null");
        Objects.requireNonNull(tasks, "tasks should be not null");
        this.id = id;
        this.executorService = executorService;
        this.tasks = List.copyOf(tasks);
    }

    @Override
    public void run() {
        this.tasks = tasks.stream()
                .map(task -> {
                    DeletionContext deletionContext = new DeletionContext();
                    task.accept(id -> deletionContext.deleted = true);
                    return Pair.of(task, deletionContext);
                })
                .filter(pair -> !pair.getRight().deleted)
                .map(Pair::getLeft)
                .collect(Collectors.toUnmodifiableList());

        if (tasks.isEmpty()) {
            LOGGER.debug("CompleteTaskWatcherSubTask with id={} is fully completed", id);
        } else {
            executorService.submit(this);
        }
    }

    private static class DeletionContext {
        volatile boolean deleted = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompleteTaskWatcherSubTask<?> that = (CompleteTaskWatcherSubTask<?>) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(executorService, that.executorService) &&
                Objects.equals(tasks, that.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, executorService, tasks);
    }

    @Override
    public String toString() {
        return "CompleteTaskWatcherSubTask{" +
                "id=" + id +
                ", executorService=" + executorService +
                ", tasks=" + tasks +
                '}';
    }
}
