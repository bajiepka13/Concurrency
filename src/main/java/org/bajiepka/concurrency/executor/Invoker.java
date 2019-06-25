package org.bajiepka.concurrency.executor;

import java.util.concurrent.Executor;

public class Invoker implements Executor {

    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
