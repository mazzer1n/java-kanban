import server.KVServer;

import javax.naming.Context;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import manager.*;
import tasks.Task;

public class Main {

    private static Context kvServer;

    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer server = new KVServer();
        server.start();

        URL kvServerUrl = new URL("http://localhost:8080");

        TaskManager manager = Managers.getHttpTaskManager(String.valueOf(kvServerUrl));

        Task task1 = new Task("Task 1:", "1");
        Task task2 = new Task("Task 2:", "2");

        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> tasks = manager.getAllTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }

        manager.deleteAllTasks();

        List<Task> tasksAfterDeletion = manager.getAllTasks();
        for (Task task : tasksAfterDeletion) {
            System.out.println(task);
        }

        kvServer.stop();
    }
}
