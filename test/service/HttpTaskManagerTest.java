package service;

import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import manager.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Status;
import tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static service.EpicTest.taskManager;

class HttpTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private static final int PORT = 8080;
    private HttpServer server;

    private static final String BASE_URL = "http://localhost:8080";

    protected HttpTaskManagerTest() throws IOException, InterruptedException {
    }

    protected HttpTaskManager createTaskManager()  {
        return new  HttpTaskManager(BASE_URL, true);
    }


    @BeforeAll
    public void setUpServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    @BeforeEach
    public void setUp() throws IOException {
        new KVTaskClient(URI.create(BASE_URL).toURL());
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.startServer();
    }

    @AfterEach
    public void stop() {
        server.stop(0);
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        // Создание тестовых данных
        Task task1 = new Task("name1", "description1", Status.NEW, Instant.now(), Duration.ofSeconds(1));
        Task task2 = new Task("name2", "description2", Status.NEW,
                Instant.now().plusSeconds(10), Duration.ofSeconds(1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        String url = "http://localhost:" + PORT + "/tasks/task/";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);

        String responseBody = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
        List<Task> tasks = new Gson().fromJson(responseBody, new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));

        HttpTaskManager httpTaskManager = new HttpTaskManager(url, true);
        assertEquals(taskManager.getAllTasks(), httpTaskManager.getAllTasks(),
                "Список задач после выгрузки не совпадает");
        List<Task> sortedTasks = taskManager.getPrioritizedTasks();
        assertEquals(sortedTasks, httpTaskManager.getAllTasks(),
                "Отсортированный список задач не совпадает");
    }

    @Test
    void testHistoryManager() throws IOException, InterruptedException {


        FileBackedTasksManager taskManager = new FileBackedTasksManager("test_task.csv");
        String url = "http://localhost:" + PORT + "/tasks/history";
        HttpTaskManager httpTaskManager = new HttpTaskManager(url);

        taskManager = TaskService.loadFromFile("test_task.csv");

        List<Task> fileHistory = taskManager.getHistory();
        List<Task> serverHistory = httpTaskManager.getHistory();

        assertEquals(fileHistory, serverHistory);
    }

}