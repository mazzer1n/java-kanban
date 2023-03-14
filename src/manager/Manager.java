package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Manager {
    private int nextId;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epicTasks;
    private HashMap<Integer, Task> tasks;

    public Manager() {
        this.nextId = 0;
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int generateId() {
        return nextId++;
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epicTasks.get(id);
    }

    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epicTasks.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epicTasks.get(subtask.getEpicId()));
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epicTasks.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                return;
            }
        }
        epicTasks.put(epic.getId(), epic);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epicTasks.get(subtask.getEpicId());
        subtasks.remove(id);
        updateEpicStatus(epic);
    }

    public void deleteEpicById(int id) {
        Iterator<Map.Entry<Integer, Subtask>> iterator = subtasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Subtask> entry = iterator.next();
            Subtask subtask = entry.getValue();
            if (subtask.getEpicId() == id) {
                iterator.remove();
            }
        }
        epicTasks.remove(id);
    }

    private void updateEpicStatus(Epic epic) {
        int statusNew = 0;
        int statusInProgress = 0;
        int statusDone = 0;
        int epicId = epic.getId();
        int subtaskCount = 0;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                if (subtask.getStatus().equals("NEW")) {
                    ++statusNew;
                } else if (subtask.getStatus().equals("IN_PROGRESS")) {
                    ++statusInProgress;
                } else if (subtask.getStatus().equals("DONE")) {
                    ++statusDone;
                }
                ++subtaskCount;
            }
        }

        if (statusNew == subtaskCount) {
            epic.setStatus("NEW");
        } else if (statusDone == subtaskCount) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
        epicTasks.put(epic.getId(), epic);
    }

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getSubtaskOfEpic(Epic epic) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                subtasksOfEpic.add(subtask);
            }
        }
        return subtasksOfEpic;
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epicTasks.values()) {
            epic.setStatus("NEW");
        }
    }

    public void clearEpic() {
        epicTasks.clear();
        subtasks.clear();
    }

}






