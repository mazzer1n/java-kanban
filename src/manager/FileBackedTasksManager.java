package manager;

import exception.ManagerSaveException;
import tasks.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    void checkAppend(boolean append) {

    }

    public static FileBackedTasksManager loadFromFile(File file) {

    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        updateSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    private void save() throws ManagerSaveException {
        String strHistory = "";
        final List<Task> allTask = new ArrayList<>();
        allTask.addAll((Collection<? extends Task>) tasks);
        allTask.addAll((Collection<? extends Task>) epics);
        allTask.addAll((Collection<? extends Task>) subtasks);

        try (FileWriter writer = new FileWriter(file)) {
            for (Task task : allTask) {
                writer.write(TaskService.toString(task));
            }
            writer.write("");
            writer.write(TaskService.historyToString(historyManager));

        } catch (IOException ex) {
            throw new ManagerSaveException();
        }

    }

}
