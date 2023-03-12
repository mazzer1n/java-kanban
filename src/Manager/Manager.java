package Manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;



public class Manager {
    private int nextId;
    HashMap<Integer,Subtask> subtasks;
    HashMap<Integer,Epic> epicTasks;
    HashMap<Integer,Task> tasks;
   public Manager() {
        this.nextId = 0;
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int generateId() {
        return nextId++;
    }


    public void clearTasks() {
        tasks.clear();
        epicTasks.clear();
        subtasks.clear();
        nextId = 0;
    }

    public Object getTaskById(int id) {
        if (subtasks.containsKey(id)) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getId() == id) {
                    return subtask;
                }
            }
        } else if (tasks.containsKey(id)) {
            for (Task task : tasks.values()) {
                if (task.getId() == id) {
                    return task;
                }
            }
        } else if (epicTasks.containsKey(id)) {
            for (Epic epic : epicTasks.values()) {
                if (epic.getId() == id) {
                    return epic;
                }
            }
        }
        return null;
        }


    public void addTask(Object o) {
        String taskType = o.getClass().getName();

        switch (taskType) {
            case "Tasks.Epic":
                Epic epic = (Epic) o;
                epic.setId(generateId());
                epicTasks.put(epic.getId(),epic);
                break;
            case "Tasks.Task":
                Task task = (Task) o;
                task.setId(generateId());
                tasks.put(task.getId(),task);
                break;

        }

    }

    public void addSubtask(Subtask subtask, Epic epic) {
        subtask.setId(generateId());
        subtask.setEpicId(epic.getId());
        subtasks.put(subtask.getId(),subtask);
    }
    public void updateTask(Object o) {
        String taskType = o.getClass().getName();

        switch (taskType) {
            case "Tasks.Subtask":
                Subtask subtask = (Subtask) o;
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epicTasks.get(subtask.getEpicId());
                if (epic != null) {
                    controlProgressEpic(epic);
                }
                break;
            case "Tasks.Task":
                Task task = (Task) o;
                tasks.put(task.getId(), task);
                break;
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
       Subtask subtask = subtasks.get(id);
       Epic epic = epicTasks.get(subtask.getEpicId());
       subtasks.remove(id);
       controlProgressEpic(epic);
    }

   public void deleteEpicById(int id) {
        epicTasks.remove(id);
    }

    private void controlProgressEpic(Epic epic) {
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
        } else if (counter == 0 && !epic.getStatus().equals("DONE")) {
            epic.setStatus("NEW");
        }
        epicTasks.put(epic.getId(), epic);
    }

    public ArrayList<Object> getTasksList() {
        ArrayList<Object> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epicTasks.values());
        allTasks.addAll(subtasks.values());
        return allTasks;
    }

   public ArrayList<Subtask> getSubtaskOfEpic(Epic epic) {
        ArrayList<Subtask> subtasksOfEpic= new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                subtasksOfEpic.add(subtask);
            }
        }
        return subtasksOfEpic;
    }

}






