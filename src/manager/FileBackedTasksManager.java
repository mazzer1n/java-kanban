package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;
    FileBackedTasksManager(File file) {
        super();
        this.file = file;

    }
    FileBackedTasksManager(File file, boolean append) {
        super();
        this.file = file;

    }

    void checkAppend (boolean append) {

    }

    public static FileBackedTasksManager loadFromFile(File file) {

    }

    @Override
    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    String toString(Task task) {}

    Task fromString(String value) {}

    static String historyToString(HistoryManager manager) {}

    static List<Integer> historyFromString(String value) {}

}
