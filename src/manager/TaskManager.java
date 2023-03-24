package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;


public interface TaskManager {
    public Subtask getSubtaskById(int id);


    public Task getTaskById(int id);


    public Epic getEpicById(int id);

    public void addTask(Task task);


    public void addEpic(Epic epic);


    public void addSubtask(Subtask subtask);


    public void updateSubtask(Subtask subtask);


    public void updateTask(Task task);


    public void updateEpic(Epic epic);


    public void deleteTaskById(int id);


    public void deleteSubtaskById(int id);


    public void deleteEpicById(int id);


    public void updateEpicStatus(Epic epic);


    public ArrayList<Task> getTaskList();


    public ArrayList<Epic> getEpicList();


    public ArrayList<Subtask> getSubtaskList();


    public ArrayList<Subtask> getSubtaskOfEpic(Epic epic);


    public void clearTasks();


    public void clearSubtasks();

    public void clearEpic();


}

