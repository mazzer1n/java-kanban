import server.KVServer;

import javax.naming.Context;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import manager.*;
import tasks.Status;
import tasks.Task;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer server = new KVServer();
        server.start();

        URL kvServerUrl = new URL("http://localhost:8080");

        TaskManager manager = Managers.getHttpTaskManager(String.valueOf(kvServerUrl));

        Task task1 = new Task("name1", "description1", Status.NEW, Instant.now(), Duration.ofSeconds(1));
        Task task2 = new Task("name2", "description2", Status.NEW,
                Instant.now().plusSeconds(10), Duration.ofSeconds(1));

        manager.addTask(task1);
        manager.addTask(task2);

        List<Task> tasks = manager.getAllTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }

        manager.deleteAllTasks();

        List<Task> tasksAfterDeletion = manager.getAllTasks();
        for (Task task : tasksAfterDeletion) {
            System.out.println(task);
        }

        server.stop();
    }
}
