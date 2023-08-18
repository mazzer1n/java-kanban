package client;

import exception.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String serverUrl;
    private final String apiToken;

    public KVTaskClient(URL serverUrl)  {
        this.serverUrl = String.valueOf(serverUrl);
        this.apiToken = register();
    }

    private String register() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/register"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new ManagerSaveException("Ошибка регистрации на сервере.");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка регистрации на сервере.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String key, String json) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/save/" + key + "?API_TOKEN=" + apiToken))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Ошибка сохранения данных с сервера.");
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка сохранения данных с сервера.");
        }
    }

    public String load(String key) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/load/" + key + "?API_TOKEN=" + apiToken))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new ManagerSaveException("Ошибка загрузки данных с сервера.");
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка загрузки данных с сервера.");
        }
    }
}
