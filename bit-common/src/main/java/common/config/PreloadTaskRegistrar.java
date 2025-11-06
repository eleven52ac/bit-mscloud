package commons.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class PreloadTaskRegistrar {

    private final BlockingDeque<Runnable> taskQueue = new LinkedBlockingDeque<>();

    public void register(Runnable task) {
        taskQueue.add(task);
    }

    public Runnable pollTask() {
        return taskQueue.poll();
    }

    public boolean hasMoreTasks() {
        return !taskQueue.isEmpty();
    }
}
