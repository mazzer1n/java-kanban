package service;

import com.google.gson.Gson;
import manager.HttpTaskManager;
import manager.Managers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {

    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void startServer() throws IOException {
        HttpTaskManager taskManager = new HttpTaskManager(BASE_URL, true);
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.startServer();
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/task"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/task?id="))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testCreateAndUpdateTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/task"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\": \"Test Task\", \"description\": \"Test Description\"}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        int taskId = 1;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/task?id=" + taskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/task/"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testCreateEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/epics/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Test Epic\", \"description\": \"Test Description\"}"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        int epicId = 1;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/epics/?id=" + epicId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/task"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Test Task\", \"description\": \"Test Description\"}"))
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        String responseBody = createResponse.body();
        Task createdTask = Managers.createCustomGson().fromJson(responseBody, Task.class);

        createdTask.setName("Updated Test Task");
        createdTask.setDescription("Updated Test Description");

        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/task"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(Managers.createCustomGson().toJson(createdTask)))
                .build();

        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, updateResponse.statusCode());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/subtask/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Test Subtask\", \"description\": \"Test Description\"}"))
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        String responseBody = createResponse.body();
        Subtask createdSubtask = Managers.createCustomGson().fromJson(responseBody, Subtask.class);

        createdSubtask.setName("Updated Test Subtask");
        createdSubtask.setDescription("Updated Test Description");

        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/subtask/"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(Managers.createCustomGson().toJson(createdSubtask)))
                .build();

        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, updateResponse.statusCode());
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/epics/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Test Epic\", \"description\": \"Test Description\"}"))
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, createResponse.statusCode());

        String responseBody = createResponse.body();
        Epic createdEpic = Managers.createCustomGson().fromJson(responseBody, Epic.class);

        createdEpic.setName("Updated Test Epic");
        createdEpic.setDescription("Updated Test Description");

        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/epics/"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(Managers.createCustomGson().toJson(createdEpic)))
                .build();

        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, updateResponse.statusCode());
    }

    @Test
    public void testGetSubtasksOfEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/epics/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\": \"Test Epic\", \"description\": \"Test Description\"}"))
                .build();

        HttpResponse<String> createEpicResponse = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, createEpicResponse.statusCode());

        String epicResponseBody = createEpicResponse.body();
        Epic createdEpic = Managers.createCustomGson().fromJson(epicResponseBody, Epic.class);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", Status.IN_PROGRESS, createdEpic.getId(),
                Instant.now(), Duration.ofSeconds(1));
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", Status.DONE, createdEpic.getId(),
                Instant.now().plusSeconds(10), Duration.ofHours(1));

        HttpRequest createSubtaskRequest1 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/subtask/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Managers.createCustomGson().toJson(subtask1)))
                .build();

        HttpRequest createSubtaskRequest2 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/subtask/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Managers.createCustomGson().toJson(subtask2)))
                .build();

        HttpResponse<String> createSubtaskResponse1 = client.send(createSubtaskRequest1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> createSubtaskResponse2 = client.send(createSubtaskRequest2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, createSubtaskResponse1.statusCode());
        assertEquals(201, createSubtaskResponse2.statusCode());

        HttpRequest getSubtasksRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks/subtask/epic/?id=" + createdEpic.getId()))
                .GET()
                .build();

        HttpResponse<String> getSubtasksResponse = client.send(getSubtasksRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, getSubtasksResponse.statusCode());
    }


}