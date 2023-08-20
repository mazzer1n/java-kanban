package manager;

import exception.ManagerSaveException;
import exception.ManagerUpdateException;
import tasks.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected int nextId;
    protected final Map<Integer, Subtask> subtasks;

    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Task> tasks;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));


    public InMemoryTaskManager() {
        this.nextId = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    protected int generateId() {
        return ++nextId;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void addTask(Task task) {
        checkOverlaps(task);
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        checkOverlaps(subtask);
        subtask.setId(generateId());
        final Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask saved = subtasks.get(subtask.getId());
        if (saved == null || subtasks.isEmpty()) {
            throw new ManagerUpdateException("Invalid subtask or subtasks list is empty");
        }
        prioritizedTasks.remove(saved);
        saved.setName(subtask.getName());
        saved.setStatus(subtask.getStatus());
        saved.setDescription(subtask.getDescription());
        saved.setStartTime(subtask.getStartTime());
        saved.setDuration(subtask.getDuration());
        prioritizedTasks.add(saved);
        final Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }

    @Override
    public void updateTask(Task task) {
        Task saved = tasks.get(task.getId());
        if (saved == null || tasks.isEmpty()) {
            throw new ManagerUpdateException("Invalid task or tasks list is empty");
        }
        prioritizedTasks.remove(saved);
        saved.setName(task.getName());
        saved.setStatus(task.getStatus());
        saved.setDescription(task.getDescription());
        saved.setStartTime(task.getStartTime());
        saved.setDuration(task.getDuration());
        prioritizedTasks.add(saved);
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic saved = epics.get(epic.getId());
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
    }

    @Override
    public void deleteTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);
        final Epic epic = epics.get(subtask.getEpicId());
        epic.deleteSubtaskId(id);
        prioritizedTasks.remove(subtask);
        subtasks.remove(id);
        updateEpicStatus(epic);
        updateEpicTime(epic);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);

        for (Integer subtaskId : epic.getSubtasksId()) {
            prioritizedTasks.remove(subtasks.remove(subtaskId));
            historyManager.remove(subtaskId);
        }

        epics.remove(id);
        historyManager.remove(id);
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
    }

    protected void updateEpicTime(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            return;
        } else if (epic.getSubtasksId().size() == 1) {
            Subtask subtask = subtasks.get(epic.getSubtasksId().get(0));
            epic.setStartTime(subtask.getStartTime());
            epic.setDuration(subtask.getDuration());
            epic.setEndTime(subtask.getEndTime());
            return;
        }
        for (Integer id : epic.getSubtasksId()) {
            Duration duration = epic.getDuration();
            Instant startTime = epic.getStartTime();
            Instant endTime = epic.getEndTime();
            Subtask subtask = subtasks.get(id);
            if (startTime == null && endTime == null && duration.isZero()) {
                epic.setStartTime(subtask.getStartTime());
                epic.setDuration(subtask.getDuration());
                epic.setEndTime(subtask.getEndTime());
            } else if (startTime.isAfter(subtask.getStartTime())) { //Время старта по умолчанию Instant.now()
                epic.setStartTime(subtask.getStartTime());
                epic.setDuration(duration.plus(subtask.getDuration()));
            } else if (endTime.isBefore(subtask.getEndTime())) { // по умолчанию duration.zero, время конца всегда будет
                epic.setEndTime(subtask.getEndTime());
                epic.setDuration(duration.plus(subtask.getDuration()));
            } else {
                epic.setDuration(duration.plus(subtask.getDuration()));
            }

        }
    }

    protected boolean hasTimeOverlap(Task task1, Task task2) {
        return !task1.getEndTime().isBefore(task2.getStartTime()) && !task1.getStartTime().isAfter(task2.getEndTime());
    }

    protected void checkOverlaps(Task task) {
        List<Task> sortedTasks = new ArrayList<>(prioritizedTasks);

        sortedTasks.sort(Comparator.comparing(Task::getStartTime));

        for (int i = 0; i < sortedTasks.size(); ++i) {
            Task currentTask = sortedTasks.get(i);
            if (currentTask.getId() != task.getId()) {
                if (hasTimeOverlap(task, currentTask)) {
                    throw new ManagerSaveException("Tasks is overlaps");
                }
            }
        }
    }


    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(Epic epic) {
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
        removeAllTasks();
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
            epic.clearSubtasksId();
            updateEpicTime(epic);
        }

        for (Integer subtaskId : subtasks.keySet())
            historyManager.remove(subtaskId);

        subtasks.clear();
        removeAllSubtasks();


    }

    @Override
    public void clearEpics() {
        for (Integer epicId : epics.keySet())
            historyManager.remove(epicId);

        epics.clear();
        clearSubtasks();

    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void clearHistory() {
        historyManager.clear();
    }

    public List<Task> getPrioritizedTasks() {
        return List.copyOf(prioritizedTasks);
    }

    public List<Task> getAllTasks() {
        final List<Task> allTask = new ArrayList<>();
        allTask.addAll(getTaskList());
        allTask.addAll(getEpicList());
        allTask.addAll(getSubtaskList());
        return allTask;
    }

    @Override
    public void deleteAllTasks() {
        clearTasks();
        clearEpics();
        clearSubtasks();
    }

    private void removeAllTasks() {
        prioritizedTasks.removeIf(task -> !(task instanceof Subtask));
    }
    // не очень понял про какие эпики речь? Это удаление из дерева, там нет эпиков

    private void removeAllSubtasks() {
        prioritizedTasks.removeIf(task -> task instanceof Subtask);

    }


}







