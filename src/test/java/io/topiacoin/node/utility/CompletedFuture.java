package io.topiacoin.node.utility;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CompletedFuture<T> implements Future<T> {
    private T result;
    private Throwable throwable;
    private boolean interrupted;

    public CompletedFuture(T result) {
        this.result = result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        if (this.throwable != null) {
            throw new ExecutionException(this.throwable);
        }
        if (this.interrupted) {
            throw new InterruptedException();
        }
        return result;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return result;
    }

    public void setException(Throwable t) {
        this.throwable = t;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }
}
