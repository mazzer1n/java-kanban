package server;

import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import exception.InterruptedException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void startServer() throws IOException {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/", new TaskHandler(taskManager));
            server.setExecutor(null);
            server.start();

            System.out.println("Сервер запущен на порту " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getFileBackedTaskManager("/Users/maksimmalyarov/IdeaProjects/java-kanban1/tasks.txt");

        new HttpTaskServer(taskManager).startServer();
    }

    private static class TaskHandler implements HttpHandler {

        private final Gson gson;
        private final TaskManager taskManager;
        private KVTaskClient kvTaskClient = new KVTaskClient(new URL("http://localhost:8080"));

        public TaskHandler(TaskManager taskManager) throws MalformedURLException {
            this.taskManager = taskManager;
            gson = new GsonBuilder().create();
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if (method.equals("GET") && path.equals("/tasks/task/")) {
                List<Task> tasks = taskManager.getTaskList();
                String response = gson.toJson(tasks);
                sendResponse(exchange, response);
            } else if (method.equals("GET") && path.equals("/tasks/")) {
                List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String response = gson.toJson(prioritizedTasks);
                sendResponse(exchange, response);
            } else if (method.equals("GET") && path.equals("/tasks/subtask/")) {
                List<Subtask> subtasks = taskManager.getSubtaskList();
                String response = gson.toJson(subtasks);
                sendResponse(exchange, response);
            } else if (method.equals("GET") && path.equals("/tasks/epics/")) {
                List<Epic> epics = taskManager.getEpicList();
                String response = gson.toJson(epics);
                sendResponse(exchange, response);
            } else if (method.equals("GET") && path.startsWith("/tasks/task/?id=")) {
                int taskId = parseFromPathIfEqual(path);
                Task task = null;
                try {
                    task = taskManager.getTaskById(taskId);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (task != null) {
                    String response = gson.toJson(task);
                    sendResponse(exchange, response);
                } else {
                    sendResponse(exchange, "Задача не найдена", HttpURLConnection.HTTP_NOT_FOUND);
                }
            } else if (method.equals("GET") && path.startsWith("/tasks/subtask/?id=")) {
                int subtaskId = parseFromPathIfEqual(path);
                Subtask subtask = null;
                try {
                    subtask = taskManager.getSubtaskById(subtaskId);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (subtask != null) {
                    String response = gson.toJson(subtask);
                    sendResponse(exchange, response);
                } else {
                    sendResponse(exchange, "Подзадача не найдена", HttpURLConnection.HTTP_NOT_FOUND);
                }
            } else if (method.equals("GET") && path.startsWith("/tasks/epics/?id=")) {
                int epicId = parseFromPathIfEqual(path);
                Epic epic = null;
                try {
                    epic = taskManager.getEpicById(epicId);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (epic != null) {
                    String response = gson.toJson(epic);
                    sendResponse(exchange, response);
                } else {
                    sendResponse(exchange, "Эпик не найден", HttpURLConnection.HTTP_NOT_FOUND);
                }
            } else if (method.equals("GET") && path.startsWith("/tasks/subtask/epic/?id=")) {
                int epicId = parseFromPathIfEqual(path);
                Epic epic = null;
                try {
                    epic = taskManager.getEpicById(epicId);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (epic != null) {
                    List<Subtask> subtasksOfEpic = taskManager.getSubtasksOfEpic(epic);
                    String response = gson.toJson(subtasksOfEpic);
                    sendResponse(exchange, response);
                } else {
                    sendResponse(exchange, "Эпик не найден", HttpURLConnection.HTTP_NOT_FOUND);
                }
            } else if (method.equals("POST")) {
                if (path.equals("/tasks/task/")) {
                    InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    Task newTask = gson.fromJson(reader, Task.class);


                    kvTaskClient.put("task_" + newTask.getId(), gson.toJson(newTask));

                    taskManager.addTask(newTask);
                    sendResponse(exchange, "Задача успешно создана", HttpURLConnection.HTTP_OK);
                } else if (path.equals("/tasks/subtask/")) {
                    InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    Subtask newSubtask = gson.fromJson(reader, Subtask.class);


                    kvTaskClient.put("subtask_" + newSubtask.getId(), gson.toJson(newSubtask));

                    taskManager.addSubtask(newSubtask);
                    sendResponse(exchange, "Подзадача успешно создана", HttpURLConnection.HTTP_OK);
                } else if (path.equals("/tasks/epics/")) {
                    InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
                    Epic newEpic = gson.fromJson(reader, Epic.class);


                    kvTaskClient.put("epic_" + newEpic.getId(), gson.toJson(newEpic));

                    taskManager.addEpic(newEpic);
                    sendResponse(exchange, "Эпик успешно создан", HttpURLConnection.HTTP_OK);
                } else {
                    sendResponse(exchange, "Метод не поддерживается", HttpURLConnection.HTTP_BAD_METHOD);
                }
            } else if (method.equals("DELETE")) {
                if (path.startsWith("/tasks/task/?id=")) {
                    int taskId = parseFromPathIfEqual(path);


                    taskManager.deleteTaskById(taskId);
                    kvTaskClient.delete("task_" + taskId);

                    sendResponse(exchange, "Задача успешно удалена", HttpURLConnection.HTTP_OK);
                } else if (path.startsWith("/tasks/subtask/?id=")) {
                    int subtaskId = parseFromPathIfEqual(path);


                    taskManager.deleteSubtaskById(subtaskId);
                    kvTaskClient.delete("subtask_" + subtaskId);

                    sendResponse(exchange, "Подзадача успешно удалена", HttpURLConnection.HTTP_OK);
                } else if (path.startsWith("/tasks/epics/?id=")) {
                    int epicId = parseFromPathIfEqual(path);


                    taskManager.deleteEpicById(epicId);
                    kvTaskClient.delete("epic_" + epicId);

                    sendResponse(exchange, "Эпик успешно удален", HttpURLConnection.HTTP_OK);
                } else {
                    sendResponse(exchange, "Метод не поддерживается", HttpURLConnection.HTTP_BAD_METHOD);
                }
            } else {
                sendResponse(exchange, "Метод не поддерживается", HttpURLConnection.HTTP_BAD_METHOD);
            }
        }


        private int parseFromPathIfEqual(String path) {
            String[] parts = path.split("=");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid path format");
            }
            return Integer.parseInt(parts[1]);
        }

        private void sendResponse(HttpExchange exchange, String response, int responseCode) throws IOException {
            byte[] bytes = response.getBytes();
            exchange.sendResponseHeaders(responseCode, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        }

        private void sendResponse(HttpExchange exchange, String response) throws IOException {
            sendResponse(exchange, response, HttpURLConnection.HTTP_OK);
        }
    }
}


