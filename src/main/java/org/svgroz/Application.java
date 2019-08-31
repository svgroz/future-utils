package org.svgroz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.*;

public class Application {
    static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> submit = executorService.submit(() -> "fooBar");

        final CompletableFuture<String> completableFuture = new CompletableFuture<>();
        FutureWatcherTask<String> futureWatcherTask = new FutureWatcherTask<>(
                UUID.randomUUID(),
                submit,
                completableFuture
        );

        ExecutorService executorService1 = Executors.newFixedThreadPool(10);
        CompleteTaskWatcher<Future<String>> futureCompleteTaskWatcher = new CompleteTaskWatcher<>(
                10L,
                executorService1
        );

        CountDownLatch countDownLatch = new CountDownLatch(1);

        completableFuture.handle((s, e) -> {
            countDownLatch.countDown();
            return s;
        });

        futureCompleteTaskWatcher.makeSubTasks(Collections.singleton(futureWatcherTask));

        logger.info("Completable future await");

        countDownLatch.await();

        logger.info("Application completed");
        executorService.shutdown();
        executorService1.shutdown();
    }
}
