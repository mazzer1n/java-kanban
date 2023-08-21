package service;

import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import manager.HttpTaskManager;
import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static final int PORT = 8080;
    private static HttpServer server;

    private static final String BASE_URL = "http://localhost:8080";

    protected HttpTaskManager createTaskManager() {
        return new HttpTaskManager(BASE_URL, true);
    }

    @BeforeAll
    public static void setUpServer() throws IOException {
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
    public void testGetAllTasks() throws IOException {
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

        Gson gson = new Gson();
        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(new InputStreamReader(connection.getInputStream()), taskListType);

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));

        HttpTaskManager httpTaskManager = new HttpTaskManager(url, true);
        List<Task> httpTasks = httpTaskManager.getAllTasks();

        assertEquals(taskManager.getAllTasks(), httpTasks, "Список задач после выгрузки не совпадает");

        List<Task> sortedTasks = taskManager.getPrioritizedTasks();
        assertEquals(sortedTasks, httpTasks, "Отсортированный список задач не совпадает");

        // Проверка содержимого истории после выгрузки
        List<Task> history = taskManager.getHistory();
        List<Task> httpHistory = httpTaskManager.getHistory();

        assertEquals(history, httpHistory, "История задач не совпадает");
    }


    @Test
    public void testGetTaskHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        // Создание задачи через POST запрос
        HttpRequest createTaskRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/task"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\": \"Тестовая задача\", \"description\": \"Тестовое описание\"}"))
                .build();
        HttpResponse<String> createTaskResponse = client.send(createTaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createTaskResponse.statusCode());

        Gson gson = Managers.createCustomGson();
        Task createdTask = gson.fromJson(createTaskResponse.body(), Task.class);

        // Получение задачи через GET запрос
        HttpRequest getTaskByIdRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/task?id=" + createdTask.getId()))
                .GET()
                .build();
        HttpResponse<String> getTaskByIdResponse = client.send(getTaskByIdRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getTaskByIdResponse.statusCode());
        Task fetchedTask = gson.fromJson(getTaskByIdResponse.body(), Task.class);

        // Получение истории задачи через GET запрос
        HttpRequest getTaskHistoryRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/task/history"))
                .GET()
                .build();
        HttpResponse<String> getTaskHistoryResponse = client.send(getTaskHistoryRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getTaskHistoryResponse.statusCode());

        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(getTaskHistoryResponse.body(), taskListType);

        assertNotNull(history);
        assertTrue(history.stream().allMatch(Objects::nonNull));
        assertTrue(history.stream().anyMatch(task -> Objects.equals(task.getId(), fetchedTask.getId())));

        for (Task task : history) {
            assertNotNull(task.getName(), "Заголовок задачи не должен быть пустым");
            assertNotNull(task.getDescription(), "Описание задачи не должно быть пустым");
        }
    }
}




