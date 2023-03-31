package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;


public class InMemoryTaskManager implements TaskManager {
    private int nextId;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Task> tasks;
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    public InMemoryTaskManager() {
        this.nextId = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int generateId() {
        return nextId++;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(generateId());
        final Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        final Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic currentEpic = epics.get(epic.getId());
        currentEpic.setName(epic.getName());
        currentEpic.setDescription(epic.getDescription());
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);
        final Epic epic = epics.get(subtask.getEpicId());
        subtasks.remove(id);
        updateEpicStatus(epic);
    }

    @Override
    public void deleteEpicById(int id) {
        final Iterator<Map.Entry<Integer, Subtask>> iterator = subtasks.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Integer, Subtask> entry = iterator.next();
            final Subtask subtask = entry.getValue();
            if (subtask.getEpicId() == id) {
                iterator.remove();
            }
        }
        epics.remove(id);
    }

    private void updateEpicStatus(Epic epic) {
        int statusNew = 0;
        int statusDone = 0;
        final int epicId = epic.getId();
        int subtaskCount = 0;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                if (subtask.getStatus() == Status.NEW) {
                    ++statusNew;
                } else if (subtask.getStatus() == Status.DONE) {
                    ++statusDone;
                }
                ++subtaskCount;
            }
        }

        if (statusNew == subtaskCount) {
            epic.setStatus(Status.NEW);
        } else if (statusDone == subtaskCount) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskOfEpic(Epic epic) {
        final ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId()) {
                subtasksOfEpic.add(subtask);
            }
        }
        return subtasksOfEpic;
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void clearEpic() {
        epics.clear();
        subtasks.clear();
    }

}
//mdnjkds






