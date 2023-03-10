import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Manager {
    int nextId;
    ArrayList<Object> tasks; // Здесь хранятся только Epic и Task (для привязки SubTask к Epic)
    HashMap<Integer, Object> identifier; // Здесь хранятся все типы задач в разброс, где ключ - id

    Manager() {
        this.nextId = 0;
        tasks = new ArrayList<>();
        identifier = new HashMap<>();
    }

    int generateId() {
        int id = nextId;
        ++nextId;
        return id;
    }

    void printAllTasks() {
        int i = 1;
        if (tasks != null) {
            for (Object o : tasks) {
                if (o.getClass().getName().equals("Epic")) {
                    int j = 1;
                    Epic epic = (Epic) o;
                    System.out.println("Задача " + i + ":" + epic.name);
                    if (!epic.subtasks.isEmpty()) {
                        System.out.println("Подзадачи:");
                        for (Subtask subtask : epic.subtasks) {
                            System.out.println(j + " - " + subtask.name);
                            ++j;
                        }
                    }
                } else {
                    Task task = (Task) o;
                    System.out.println("Задача " + i + ":" + task.name);
                }
                ++i;
            }
        } else {
            System.out.println("Список задач пуст");
            return;
        }
    }

    void clearTasks() {
        tasks.clear();
        identifier.clear();
        nextId = 0;
        System.out.println("Список задач пуст");
    }

    Object getTaskById(int idTask) {
        for (Integer id : identifier.keySet()) {
            if (id == idTask) {
                return identifier.get(id);
            }
        }
        return null;
    }

    void addTask(Object o) {
        String taskType = o.getClass().getName();

        switch (taskType) {
            case "Epic":
                tasks.add(o);
                Epic epic = (Epic) o;
                epic.id = generateId();
                identifier.put(epic.id, epic);
                if (!epic.subtasks.isEmpty()) {
                    for (Subtask subtask : epic.subtasks) {
                        subtask.id = generateId();
                        identifier.put(subtask.id, subtask);
                    }
                }
                break;
            case "Subtask":
                System.out.println("Введите идентификатор основной задачи:");
                System.out.println("Доступные идентификаторы:");
                for (Object obj : identifier.values()) {
                    if (obj.getClass().getName().equals("Epic")) {
                        Epic epic1 = (Epic) obj;
                        System.out.println(epic1.id + " - " + epic1.name);
                    }
                }
                Scanner scanner = new Scanner(System.in);
                int id = scanner.nextInt();
                Subtask subtask = (Subtask) o;
                subtask.id = generateId();
                ((Epic) getTaskById(id)).subtasks.add(subtask);
                identifier.put(subtask.id, subtask);
                break;
            case "Task":
                tasks.add(o);
                Task task = (Task) o;
                task.id = generateId();
                identifier.put(task.id, task);
                break;
            default:
                System.out.println("Данные тип задач не предусмотрен");

        }

    }

    void updateTask(Object o) {  //Здесь приходит уже существующий экземпляр класса, который нужно обновить
        String taskType = o.getClass().getName();

        switch (taskType) {
            case "Epic":
                Epic epicNew = (Epic) o;
                if (epicNew.subtasks.isEmpty()) {
                    epicNew.updateTask();
                } else {
                    epicNew.сheckProgress();
                }
                int epicIndex = tasks.indexOf(getTaskById(epicNew.id));
                tasks.set(epicIndex, epicNew);
                identifier.put(epicNew.id, epicNew);
                break;
            case "Subtask":
                Subtask subtask = (Subtask) o;
                for (Object obj : tasks) {
                    if (obj.getClass().getName().equals("Epic")) {
                        Epic epic = (Epic) obj;
                        if (epic.searchSubtask(subtask)) {
                            epic.updateSubtask(subtask);
                            epic.сheckProgress();
                            int epicIndex1 = tasks.indexOf(getTaskById(epic.id));
                            tasks.set(epicIndex1, epic);
                            identifier.put(epic.id, epic);
                        }
                    }
                }
                break;
            case "Task":
                Task taskNew = (Task) o;
                taskNew.updateTask();
                int taskIndex = tasks.indexOf(getTaskById(taskNew.id));
                tasks.set(taskIndex, taskNew);
                identifier.put(taskNew.id, taskNew);
                break;
            default:
                System.out.println("Данный тип задач не предусмотрен");
        }
    }

    void deleteTaskById(int id) {
        Object o = identifier.get(id);
        identifier.remove(id);
        String taskType = o.getClass().getName();
        if (!taskType.equals("Subtask")) {
            for (Object obj : tasks) {
                if (obj == o) {
                    tasks.remove(obj);
                }
            }
        } else {
            for (Object obj : tasks) {
                if (obj.getClass().getName().equals("Epic")) {
                    Epic epic = (Epic) obj;
                    ((Epic) obj).deleteSubtaskById(id);
                }
            }
        }
    }

    void printEpic() {
        if (tasks != null) {
            for (Object o : tasks) {
                if (o.getClass().getName().equals("Epic")) {
                    int j = 1;
                    Epic epic = (Epic) o;
                    System.out.println("Задача " + ": " + epic.name);
                    if (!epic.subtasks.isEmpty()) {
                        System.out.println("Подзадачи:");
                        for (Subtask subtask : epic.subtasks) {
                            System.out.println(j + " - " + subtask.name);
                            ++j;
                        }
                    }
                }

            }
        }
    }
}






