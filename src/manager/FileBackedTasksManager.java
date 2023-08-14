package manager;

import exception.ManagerRecoveryException;
import exception.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    FileBackedTasksManager(File file, boolean append) {
        super();
        this.file = file;
        checkAppend(append);
    }

    void checkAppend(boolean append) {
        if (append) {
            recoverManager();
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        save();
        return subtasks.get(id);
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        save();
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        save();
        return epics.get(id);
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

    public void recoverSubtask(Subtask subtask) {
        final Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }

    private void save() {
        String strHistory = "";
        final List<Task> allTask = getAllTask();

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic,startTime,duration\n");
            for (Task task : allTask) {
                writer.write(TaskService.toString(task) + "\n");
            }
            writer.write("\n");
            writer.write(TaskService.historyToString(historyManager));

        } catch (IOException ex) {
            throw new ManagerSaveException();
        }

    }

    public List<Task> getAllTask() {
        final List<Task> allTask = new ArrayList<>();
        allTask.addAll(getTaskList());
        allTask.addAll(getEpicList());
        allTask.addAll(getSubtaskList());
        return allTask;
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
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file,false);
        fileBackedTasksManager.addEpic(new Epic("name", "decription"));
        fileBackedTasksManager.getEpicById(1);
        fileBackedTasksManager.addTask(new Task("nameTask", "decriptionTask", Status.IN_PROGRESS, Instant.now(),
                Duration.ofSeconds(50)));
        fileBackedTasksManager.addSubtask(new Subtask("nameSubtask","descriptionSub",Status.IN_PROGRESS,1,
                Instant.now().plusSeconds(500),Duration.ofSeconds(500)));
        fileBackedTasksManager.getTaskById(2);
        fileBackedTasksManager.getSubtaskById(3);
        FileBackedTasksManager manager2 = TaskService.loadFromFile(file);
        manager2.addEpic(new Epic("nameEpic2","decription2"));
        manager2.addSubtask(new Subtask("nameSub1","description1",Status.IN_PROGRESS,4,
                Instant.now().plusSeconds(5000),Duration.ofSeconds(5000)));
        manager2.addSubtask(new Subtask("nameSub1","description1",Status.IN_PROGRESS,4,
                Instant.now().plusSeconds(10000),Duration.ofSeconds(5000)));
        manager2.deleteSubtaskById(6);


    }

}






