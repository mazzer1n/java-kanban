package manager;

import exception.ManagerRecoveryException;
import exception.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.util.ArrayList;
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
        if (append) {

        }

    }

    @Override
    public void addTask(Task task) throws ManagerSaveException {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) throws ManagerSaveException {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) throws ManagerSaveException {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        updateSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) throws ManagerSaveException {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) throws ManagerSaveException {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) throws ManagerSaveException {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void clearTasks() throws ManagerSaveException {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() throws ManagerSaveException {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() throws ManagerSaveException {
        super.clearEpics();
        save();
    }

    private void save() throws ManagerSaveException {
        String strHistory = "";
        final List<Task> allTask = new ArrayList<>();
        allTask.addAll(getTaskList());
        allTask.addAll(getEpicList());
        allTask.addAll(getSubtaskList());

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

    private void recoveryManager(File file) throws ManagerRecoveryException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            while (fileReader.ready()) {
                String str = fileReader.readLine();
                if (TaskService.fromString(str).getTypeTask() == TypeTask.TASK) {
                    addTask(TaskService.fromString(str));
                } else if (TaskService.fromString(str).getTypeTask() == TypeTask.EPIC) {
                    addEpic((Epic) TaskService.fromString(str));
                } else {
                    addSubtask((Subtask) TaskService.fromString(str));
                }
            }
        } catch (IOException e) {
            throw new ManagerRecoveryException();
        }
    }

}

