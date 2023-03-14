package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Manager {
    private int nextId;
    HashMap<Integer, Subtask> subtasks;
    HashMap<Integer, Epic> epicTasks;
    HashMap<Integer, Task> tasks;

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
        if (subtasks.containsKey(id)) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getId() == id) {
                    return subtask;
                }
            }
        }
        return null;
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            for (Task task : tasks.values()) {
                if (task.getId() == id) {
                    return task;
                }
            }
        }
        return null;
    }

    public Epic getEpicById(int id) {
        if (epicTasks.containsKey(id)) {
            for (Epic epic : epicTasks.values()) {
                if (epic.getId() == id) {
                    return epic;
                }
            }
        }
        return null;
    }

    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epicTasks.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask, Epic epic) {
        subtask.setId(generateId());
        subtask.setEpicId(epic.getId());
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
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
        Iterator<Subtask> iterator = subtasks.values().iterator();
        while (iterator.hasNext()) {
            Subtask subtask = iterator.next();
            if (subtask.getEpicId() == id) {
                iterator.remove();
            }
        }
        epicTasks.remove(id);
    }

    private void updateEpicStatus(Epic epic) {
        int statusNew = 0;
        int statusProgress = 0;
        int statusDone = 0;
        int counter = 0;
        int epicId = epic.getId();

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                if (subtask.getStatus().equals("NEW")) {
                    ++statusNew;
                } else if (subtask.getStatus().equals("IN_PROGRESS")) {
                    ++statusProgress;
                } else {
                    ++statusDone;
                }
                ++counter;
            }
        }
        if (statusNew != counter && statusDone != counter) {
            epic.setStatus("IN_PROGRESS");
        } else if (statusDone == counter && statusDone != 0) {
            epic.setStatus("DONE");
        } else if (counter == 0) {
            epic.setStatus("NEW");
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

    public void clearTask() {
        tasks.clear();
    }

    public void clearSubtask() {
        subtasks.clear();
    }

    public void clearEpic() {
        epicTasks.clear();
    }

}






