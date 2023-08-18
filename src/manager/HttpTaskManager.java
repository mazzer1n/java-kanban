package manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.ManagerSaveException;
import manager.FileBackedTasksManager;
import client.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient taskClient;
    private final String keyTasks = "tasks";
    private final String keyEpics = "epics";
    private final String keySubtasks = "subtasks";
    private final String keyHistory = "history";
    private static final Gson gson = new Gson();

    public HttpTaskManager(String uri) {
        this(uri,false);
    }

    public HttpTaskManager(String uri, boolean shouldLoad) {
        super(uri);
        try {
            this.taskClient = new KVTaskClient(URI.create(uri).toURL());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка KVTaskClient.", e);
        }
        if (shouldLoad) {
            load();
        }
    }

    @Override
    public void save() {
        try {
            String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
            taskClient.put(keyTasks, jsonTasks);

            String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
            taskClient.put(keyEpics, jsonEpics);

            String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
            taskClient.put(keySubtasks, jsonSubtasks);


            List<Integer> historyIds = getHistory().stream().map(Task::getId).collect(Collectors.toList());
            String jsonHistory = gson.toJson(historyIds);


            taskClient.put("history", jsonHistory);

            taskClient.put("idCounter", String.valueOf(idCounter));
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка сохранения задач в KVServer.");
        }
    }
    private void load() throws ManagerSaveException {
        try {
            String jsonTasks = taskClient.load(keyTasks);
            ArrayList<Task> loadedTasks = gson.fromJson(jsonTasks, new TypeToken<List<Task>>() {}.getType());
            if (loadedTasks != null) {
                for (Task task : loadedTasks) {
                    tasks.put(task.getId(), task);
                    prioritizedTasks.add(task);
                }
            }
            String jsonEpics = taskClient.load(keyEpics);
            ArrayList<Epic> loadedEpics = gson.fromJson(jsonEpics, new TypeToken<List<Epic>>() {}.getType());
            if (loadedEpics != null) {
                for (Epic epic : loadedEpics) {
                    epics.put(epic.getId(), epic);
                }
            }

            String jsonSubtasks = taskClient.load(keySubtasks);
            ArrayList<Subtask> loadedSubtasks = gson.fromJson(jsonSubtasks, new TypeToken<List<Subtask>>() {}.getType());
            if (loadedSubtasks != null) {
                for (Subtask subtask : loadedSubtasks) {
                    subtasks.put(subtask.getId(), subtask);
                    prioritizedTasks.add(subtask);
                }
            }

            String jsonHistory = taskClient.load(keyHistory);
            ArrayList<Integer> loadedHistoryIds = gson.fromJson(jsonHistory, new TypeToken<List<Integer>>() {}.getType());
            if (loadedHistoryIds != null) {
                for (Integer taskId : loadedHistoryIds) {
                    Task task = tasks.get(taskId);
                    if (task != null) {
                        historyManager.add(task);
                    }
                }
            }
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка загрузки задач из KVServer.");
        }
    }
}