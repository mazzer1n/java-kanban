package manager;

import exception.ManagerRecoveryException;
import exception.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;
    private final TreeSet<Task> prioritizedTasks;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
        recoverManager();
    }

    public FileBackedTasksManager(File file, boolean append) {
        super();
        this.file = file;
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
        if (append) {
            recoverManager();
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    public List<Task> getAllTask() {
        final List<Task> allTask = new ArrayList<>();
        allTask.addAll(getTaskList());
        allTask.addAll(getEpicList());
        allTask.addAll(getSubtaskList());
        return allTask;
    }


    @Override
    public void addTask(Task task) {
        super.addTask(task);
        updatePrioritizedTasks();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        updatePrioritizedTasks();
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        updatePrioritizedTasks();
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        updatePrioritizedTasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        updatePrioritizedTasks();
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        updatePrioritizedTasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        updatePrioritizedTasks();
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        updatePrioritizedTasks();
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        updatePrioritizedTasks();
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        prioritizedTasks.clear();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        prioritizedTasks.clear();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        prioritizedTasks.clear();
        save();
    }

    private void updatePrioritizedTasks() {
        prioritizedTasks.clear();
        prioritizedTasks.addAll(getAllTask());
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void recoverSubtask(Subtask subtask) {
        final Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        prioritizedTasks.add(subtask);
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic,startTime,duration\n");
            for (Task task : prioritizedTasks) {
                writer.write(TaskService.toString(task) + "\n");
            }
            writer.write("\n");
            writer.write(TaskService.historyToString(historyManager));

        } catch (IOException ex) {
            throw new ManagerSaveException();
        }
    }

    private void recoverHistory(List<Integer> history) {
        final HashMap<Integer, Task> allTask = new HashMap<>();
        allTask.putAll(tasks);
        allTask.putAll(subtasks);
        allTask.putAll(epics);
        for (Integer id : history) {
            Task task = allTask.get(id);
            if (task == null) {
                continue;
            }
            if (task instanceof Subtask) {
                super.getSubtaskById(id);
            } else if (task instanceof Epic) {
                super.getEpicById(id);
            } else {
                super.getTaskById(id);
            }
        }
    }

    private void recoverManager() {
        boolean shift = false;
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            fileReader.readLine();
            String str;
            while ((str = fileReader.readLine()) != null) {
                try {
                    if (str.isEmpty()) {
                        shift = true;
                        continue;
                    }
                    if (shift) {
                        List<Integer> history = TaskService.historyFromString(str);
                        recoverHistory(history);
                        break;
                    }
                    Task task = TaskService.fromString(str);
                    int id = task.getId();
                    super.generateId();
                    if (task.getTypeTask() == TypeTask.TASK) {
                        tasks.put(id, task);
                    } else if (task.getTypeTask() == TypeTask.EPIC) {
                        epics.put(id, (Epic) task);
                    } else {
                        recoverSubtask((Subtask) task);
                    }
                    prioritizedTasks.add(task);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new ManagerRecoveryException();
        }
    }


    public static void main(String[] args) {
        File file = new File("/Users/maksimmalyarov/IdeaProjects/java-kanban1/tasks.txt");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file, false);
        fileBackedTasksManager.addEpic(new Epic("name", "description"));
        fileBackedTasksManager.getEpicById(1);
        fileBackedTasksManager.addTask(new Task("nameTask", "descriptionTask", Status.IN_PROGRESS));
        fileBackedTasksManager.addSubtask(new Subtask("nameSubtask", "descriptionSub", Status.IN_PROGRESS, 1));
        fileBackedTasksManager.getTaskById(2);
        fileBackedTasksManager.getSubtaskById(3);
        fileBackedTasksManager.addEpic(new Epic("name12", "description"));
        FileBackedTasksManager manager2 = new FileBackedTasksManager(file, true);
        manager2.addEpic(new Epic("nameEpic2", "description2"));
        manager2.addSubtask(new Subtask("nameSub1", "description1", Status.IN_PROGRESS, 4));
    }
}





