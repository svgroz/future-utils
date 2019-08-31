package org.svgroz;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

public interface TaskWatcher<T> {
    default void addTask(T task, ExecutorService executorService) {
        addTasks(Arrays.asList(task), executorService);
    }

    void addTasks(Collection<T> tasks, ExecutorService executorService);
}
