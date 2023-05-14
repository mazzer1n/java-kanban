package manager;

import exception.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public interface TaskManager {
    Subtask getSubtaskById(int id) throws ManagerSaveException;

    Task getTaskById(int id) throws ManagerSaveException;

    Epic getEpicById(int id) throws ManagerSaveException;

    void addTask(Task task) throws ManagerSaveException;

    void addEpic(Epic epic) throws ManagerSaveException;

    void addSubtask(Subtask subtask) throws ManagerSaveException;

    void updateSubtask(Subtask subtask) throws ManagerSaveException;

    void updateTask(Task task) throws ManagerSaveException;

    void updateEpic(Epic epic) throws ManagerSaveException;

    void deleteTaskById(int id) throws ManagerSaveException;

    void deleteSubtaskById(int id) throws ManagerSaveException;

    void deleteEpicById(int id) throws ManagerSaveException;

    ArrayList<Task> getTaskList();

    ArrayList<Epic> getEpicList();

    ArrayList<Subtask> getSubtaskList();

    ArrayList<Subtask> getSubtasksOfEpic(Epic epic);

    void clearTasks() throws IOException;

    void clearSubtasks() throws IOException;

    void clearEpics() throws IOException;

    List<Task> getHistory();

}

