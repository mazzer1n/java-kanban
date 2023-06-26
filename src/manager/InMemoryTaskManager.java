package manager;

import exception.ManagerSaveException;
import tasks.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public int nextId;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Task> tasks;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.nextId = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    protected int generateId() {
        return nextId++ + 1;
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
        validateTaskAndSubtaskOverlap(task);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        validateTaskAndSubtaskOverlap(epic);
        epics.put(epic.getId(), epic);
        prioritizedTasks.add(epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(generateId());
        validateTaskAndSubtaskOverlap(subtask);
        final Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        prioritizedTasks.add(subtask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask saved = subtasks.get(subtask.getId());
        saved.setName(subtask.getName());
        saved.setStatus(subtask.getStatus());
        saved.setDescription(subtask.getDescription());
        final Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
    }
    private List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());
        return allTasks;
    }

    @Override
    public void updateTask(Task task) {
        Task saved = tasks.get(task.getId());
        saved.setName(task.getName());
        saved.setStatus(task.getStatus());
        saved.setDescription(task.getDescription());
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            final Epic epic = epics.get(subtask.getEpicId());
            epic.deleteSubtaskId(id);
            subtasks.remove(id);
            updateEpicStatus(epic);
            historyManager.remove(id);
            prioritizedTasks.removeIf(task -> task.getId() == id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);

        if (epic != null) {
            for (Integer subtaskId : epic.getSubtasksId()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
                prioritizedTasks.removeIf(task -> task.getId() == subtaskId);
            }

            epics.remove(id);
            historyManager.remove(id);
            prioritizedTasks.removeIf(task -> task.getId() == id);
        }
    }

    protected void updateEpicStatus(Epic epic) {
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
    public ArrayList<Subtask> getSubtasksOfEpic(Epic epic) {
        final ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId())
                subtasksOfEpic.add(subtask);
        }
        return subtasksOfEpic;
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values())
            historyManager.remove(task.getId());

        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
            epic.clearSubtasksId();
        }

        for (Integer subtaskId : subtasks.keySet())
            historyManager.remove(subtaskId);

        subtasks.clear();
        prioritizedTasks.removeIf(task -> task instanceof Subtask);
    }

    @Override
    public void clearEpics() {
        for (Integer epicId : epics.keySet())
            historyManager.remove(epicId);

        epics.clear();
        clearSubtasks();
        prioritizedTasks.removeIf(task -> task instanceof Epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void validateTaskAndSubtaskOverlap(Task task) {
        if (hasTaskOverlap(task) || hasSubtaskOverlap(task)) {
            throw new ManagerSaveException("Task overlaps with existing tasks.");
        }
    }

    protected boolean hasTaskOverlap(Task task) {
        for (Task t : tasks.values()) {
            if (!task.equals(t) && isTimeOverlap(task.getStartTime(), task.getEndTime(), t.getStartTime(), t.getEndTime())) {
                return true;
            }
        }
        return false;
    }


    protected boolean hasSubtaskOverlap(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            for (Subtask s : subtasks.values()) {
                if (!subtask.equals(s) && s.getEpicId() == subtask.getEpicId() && isTimeOverlap(subtask.getStartTime(), subtask.getEndTime(), s.getStartTime(), s.getEndTime())) {
                    return true;
                }
            }
        }
        return false;
    }


    protected boolean isTimeOverlap(Instant start1, Instant end1, Instant start2, Instant end2) {
        if (start1 == null || start2 == null || end1 == null || end2 == null) {
            return false;
        }
        return (start1.isBefore(end2) || start1.equals(end2)) && (end1.isAfter(start2) || end1.equals(start2));
    }

    public int getNextId() {
        return nextId;
    }
}






