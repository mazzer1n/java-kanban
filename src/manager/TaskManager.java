package manager;

import exception.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


public interface TaskManager {
    Subtask getSubtaskById(int id);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    ArrayList<Task> getTaskList();

    ArrayList<Epic> getEpicList();

    ArrayList<Subtask> getSubtaskList();

    ArrayList<Subtask> getSubtasksOfEpic(Epic epic);

    void clearTasks();

    void clearSubtasks();

    void clearEpics();

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

}

