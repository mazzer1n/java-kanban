package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.*;
import tasks.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

public class HttpTaskServer {
    private static final int PORT = 8080;
    public static final TaskManager taskManager = Managers.getFileBackedTaskManager();
    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler());
        server.start();

        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) {

            URI uri = ex.getRequestURI();
            String path = uri.getPath();
            String method = ex.getRequestMethod();
            String query = uri.getQuery();
            String response;

            switch (method) {
                case "GET":

                case "POST":

                case "DELETE":

                default:
                    response = "Метод не поддерживается.";

            }

        }
//тут формируется ответ при GET, либо переадресация на client при POST и DELETE?
        private String requestGET(String path, String query) {
            String[] keyValue = query.split("=");
            Gson gson = new Gson();
            String response = "Указан неверный путь";
            if (path.contains("tasks/task")) {
                Task task = taskManager.getTaskById(Integer.parseInt(keyValue[1]));
                response = gson.toJson(task);
            } else if (path.contains("tasks/subtask")) {
                Subtask subtask = taskManager.getSubtaskById(Integer.parseInt(keyValue[1]));
                response = gson.toJson(subtask);
            } else if (path.contains("tasks/epic")) {
                Epic epic = taskManager.getEpicById(Integer.parseInt(keyValue[1]));
                response = gson.toJson(epic);
            } else if (path.contains("tasks/history")) {
                response = gson.toJson(taskManager.getHistory());

            }
            return response;
        }

        private void requestPOST(String path) {}

        private void requestDELETE(String path) {}
    }
}
