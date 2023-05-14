package manager;

import exception.ManagerRecoveryException;
import exception.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    FileBackedTasksManager(File file, boolean append) throws ManagerRecoveryException {
        super();
        this.file = file;
        checkAppend(append);
    }

    void checkAppend(boolean append) throws ManagerRecoveryException {
        if (append) {
            recoveryManager();
        }

    }

    @Override
    public Subtask getSubtaskById(int id) throws ManagerSaveException {
        historyManager.add(subtasks.get(id));
        save();
        return subtasks.get(id);
    }

    @Override
    public Task getTaskById(int id) throws ManagerSaveException {
        historyManager.add(tasks.get(id));
        save();
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) throws ManagerSaveException {
        historyManager.add(epics.get(id));
        save();
        return epics.get(id);
    }

    @Override
    public void addTask(Task task) throws ManagerSaveException {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) throws ManagerSaveException {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) throws ManagerSaveException {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        updateSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) throws ManagerSaveException {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) throws ManagerSaveException {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) throws ManagerSaveException {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void clearTasks() throws ManagerSaveException {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() throws ManagerSaveException {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() throws ManagerSaveException {
        super.clearEpics();
        save();
    }

    private void save() throws ManagerSaveException {
        String strHistory = "";
        final List<Task> allTask = getAllTask();

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
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
    private void recoveryHistory (List<Integer> history) throws ManagerSaveException {
        final HashMap<Integer,Task> allTask = new HashMap<>();
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

    private void recoveryManager() throws ManagerRecoveryException {
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
                        recoveryHistory(history);
                        break;
                    }
                        Task task = TaskService.fromString(str);
                        if (task.getTypeTask() == TypeTask.TASK) {
                            super.addTask(task);
                        } else if (task.getTypeTask() == TypeTask.EPIC) {
                            super.addEpic((Epic) task);
                        } else {
                            super.addSubtask((Subtask) task);
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
        try {
            File file = new File("/Users/maksimmalyarov/Desktop/Новая папка/tasks.txt");
            FileBackedTasksManager fileBackedTasksManager = TaskService.loadFromFile(file);
            System.out.println(fileBackedTasksManager.getHistory());
            System.out.println(fileBackedTasksManager.getTaskList());
            System.out.println(fileBackedTasksManager.getSubtaskList());
            fileBackedTasksManager.addEpic(new Epic("name","decription"));
            fileBackedTasksManager.getEpicById(4);
            System.out.println("");
            System.out.println(fileBackedTasksManager.getHistory());
            System.out.println(fileBackedTasksManager.getTaskList());
            System.out.println(fileBackedTasksManager.getSubtaskList());
            System.out.println(fileBackedTasksManager.getEpicList());
            fileBackedTasksManager.getTaskById(1);
            System.out.println("");
            System.out.println(fileBackedTasksManager.getHistory());
        } catch (ManagerRecoveryException exception) {
            System.out.println("Ошибка при восстановлении");
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }

    }

}

