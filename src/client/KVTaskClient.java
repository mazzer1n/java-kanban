package client;

import manager.HttpTaskManager;
import manager.TaskManager;
import server.HttpTaskServer;

public class KVTaskClient {
    TaskManager clientTaskManager = HttpTaskServer.taskManager; // Так должно быть?
    // тут ниже уже должна быть отправка инфы с менеджера на KVServer?

}
