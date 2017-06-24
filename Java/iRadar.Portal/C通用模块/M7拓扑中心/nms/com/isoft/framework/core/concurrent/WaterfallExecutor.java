package com.isoft.framework.core.concurrent;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


/**
 * @author Seth
 */
public class WaterfallExecutor {
    public static void waterfall(Iterable<ExecutorService> executors, Callable<Callable<?>> callable) throws InterruptedException, ExecutionException {
        waterfall(executors.iterator(), callable);
    }

    /**
     * This function recursively calls the {@link WaterfallCallable} tasks with the given chain of ExecutorServices.
     */
    @SuppressWarnings("unchecked")
    private static void waterfall(Iterator<ExecutorService> executors, Callable<Callable<?>> callable) throws InterruptedException, ExecutionException {
        // Fetch the next ExecutorService
        ExecutorService executor = null;
        try {
            executor = executors.next();
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("Not enough executors to service this Future task: " + callable);
        }

        if (executor == null) {
            throw new IllegalStateException("Not enough executors to service this Future task: " + callable);
        }

        // Submit the task to the current ExecutorService
        Future<Callable<?>> task = executor.submit(callable);

        Callable value = task.get();
        if (value != null) {
            // Send the return value to the next ExecutorService
            waterfall(executors, value);
        }
        // The {@link WaterfallCallable} returned null; this terminates the chain of execution
    }
}
