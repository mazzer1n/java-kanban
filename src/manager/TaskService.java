package manager;

import exception.ManagerRecoveryException;
import tasks.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    public TaskService() {
    }

    protected static Task fromString(String value) {
        String[] split = value.split(",");
        TypeTask typeTask = TypeTask.valueOf(split[1]);
        switch (typeTask) {
            case SUBTASK:
                return new Subtask(split[2], split[4], Status.valueOf(split[3]),
                        Integer.parseInt(split[5]), Integer.parseInt(split[0]));
            case TASK:
                return new Task(split[2], split[4], Status.valueOf(split[3]), Integer.parseInt(split[0]));
            case EPIC:
                return new Epic(split[2], split[4], Integer.parseInt(split[0]));
            default:
                return null;
        }
    }

    protected static String toString(Task task) {
        String strTask;
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            strTask = String.format("%d,%S,%s,%S,%s,%d", subtask.getId(), String.valueOf(TypeTask.SUBTASK),
                    subtask.getName(), String.valueOf(subtask.getStatus()), subtask.getDescription(), subtask.getEpicId());
        } else {
            strTask = String.format("%d,%S,%s,%S,%s", task.getId(), String.valueOf(task.getTypeTask()), task.getName(),
                    String.valueOf(task.getStatus()), task.getDescription());
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
