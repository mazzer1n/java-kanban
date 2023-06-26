package manager;

import exception.ManagerRecoveryException;
import exception.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
        recoverManager();
    }

    public FileBackedTasksManager(File file, boolean append) {
        super();
        this.file = file;
        if (append) {
            recoverManager();
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(epics.values());
        allTasks.addAll(subtasks.values());
        return allTasks;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic,startTime,duration\n");
            for (Task task : prioritizedTasks) {
                validateTaskAndSubtaskOverlap(task);
                writer.write(TaskService.toString(task) + "\n");
            }
            writer.write("\n");
            writer.write(TaskService.historyToString(historyManager));

        } catch (IOException ex) {
            throw new ManagerSaveException();
        }
    }

    private void recoverSubtask(Subtask subtask) {
        final Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        prioritizedTasks.add(subtask);
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





