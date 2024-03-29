package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;


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

    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<Subtask> getSubtaskList();

    List<Subtask> getSubtasksOfEpic(Epic epic);

    void clearTasks();

    void clearSubtasks();

    void clearEpics();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    List<Task> getAllTasks();

    void deleteAllTasks();
}

