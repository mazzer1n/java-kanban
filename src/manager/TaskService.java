package manager;

import exception.ManagerRecoveryException;
import tasks.*;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import tasks.*;

public class TaskService {

    public TaskService() {
    }

    protected static Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        TypeTask typeTask = TypeTask.valueOf(split[1]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        if (typeTask == TypeTask.SUBTASK) {
            int epicId = Integer.parseInt(split[5]);
            Instant startTime = Instant.parse(split[6]);
            Duration duration = Duration.ofSeconds(Long.parseLong(split[7]));
            return new Subtask(name, description, status, epicId, id, startTime, duration);
        } else if (typeTask == TypeTask.TASK) {
            Instant startTime = Instant.parse(split[6]);
            Duration duration = Duration.ofSeconds(Long.parseLong(split[7]));
            return new Task(name, description, status, id, startTime, duration);
        } else if (typeTask == TypeTask.EPIC) {
            //Duration duration = Duration.ofSeconds(Long.parseLong(split[6]));
           // Instant endTime = Instant.parse(split[7]);
            return new Epic(name, description, id);
        }

        return null;
    }


    protected static String toString(Task task) {
        String strTask;
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            strTask = String.format("%d,%S,%s,%S,%s,%d,%s,%d", subtask.getId(), String.valueOf(TypeTask.SUBTASK),
                    subtask.getName(), String.valueOf(subtask.getStatus()), subtask.getDescription(),
                    subtask.getEpicId(), subtask.getStartTime().toString(), subtask.getDuration());
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            strTask = String.format("%d,%S,%s,%S,%s,%d,%s,%d", epic.getId(), String.valueOf(epic.getTypeTask()),
                    epic.getName(), String.valueOf(epic.getStatus()), epic.getDescription(), epic.getDuration(),
                    epic.getEndTime().toString(), epic.getDuration());
        } else {
            strTask = String.format("%d,%S,%s,%S,%s,%s,%d", task.getId(), String.valueOf(task.getTypeTask()), task.getName(),
                    String.valueOf(task.getStatus()), task.getDescription(), task.getStartTime().toString(), task.getDuration());
        }
        return strTask;
    }


    protected static String historyToString(HistoryManager manager) {
        final List<Task> history = manager.getHistory();
        StringBuilder strHistory = new StringBuilder();
        for (int i = 0; i < history.size(); ++i) {
            Task task = history.get(i);
            strHistory.append(String.valueOf(task.getId()));
            if (i != history.size() - 1) {
                strHistory.append(",");
            }
        }
        return strHistory.toString();
    }

    protected static List<Integer> historyFromString(String value) {
        String[] split = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String s : split) {
            history.add(Integer.parseInt(s));
        }
        return history;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        return new FileBackedTasksManager(file, true);
    }

}
