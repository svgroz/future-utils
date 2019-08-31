package org.svgroz;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.*;

public class CompleteTaskWatcherTest {
    private ExecutorService executeOn;

    @Before
    public void before() {
        this.executeOn = Executors.newFixedThreadPool(12);
    }


    @After
    public void after() {
        if (this.executeOn != null) {
            this.executeOn.shutdown();
        }
    }

    @Test
    public void test1() throws Exception {
        Future<String> submit = executeOn.submit(() -> "fooBar");

        final CompletableFuture<String> completableFuture = new CompletableFuture<>();
        FutureWatcherTask<String> futureWatcherTask = new FutureWatcherTask<>(
                UUID.randomUUID(),
                submit,
                completableFuture
        );

        CompleteTaskWatcher<Future<String>> futureCompleteTaskWatcher = new CompleteTaskWatcher<>(
                10L
        );

        CountDownLatch countDownLatch = new CountDownLatch(1);

        completableFuture.handle((s, e) -> {
            countDownLatch.countDown();
            return s;
        });

        futureCompleteTaskWatcher.addTasks(Collections.singleton(futureWatcherTask), executeOn);

        countDownLatch.await();

        Assert.assertEquals("fooBar", completableFuture.get());
    }
}
