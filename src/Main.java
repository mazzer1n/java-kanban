import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;


public class Main {

    public static void main(String[] args) {

        /*TaskManager taskManager = Managers.getDefault();


        Task task1 = new Task("name1", "description1", Status.NEW);
        Task task2 = new Task("name2", "description2", Status.NEW);
        Epic epic1 = new Epic("nameEpic1", "descriptionEpic1");
        Epic epic2 = new Epic("nameEpic2", "descriptionEpic2");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        System.out.println(task1.getId()); //id = 0
        System.out.println(task2.getId()); //id = 1
        System.out.println(epic1.getId()); //id = 2
        System.out.println(epic2.getId()); //id = 3

        Subtask subtask11 = new Subtask("subtask11", "descriptionSubtask11", Status.NEW, 2);
        Subtask subtask12 = new Subtask("subtask12", "descriptionSubtask12", Status.NEW, 2);
        Subtask subtask13 = new Subtask("subtask13", "descriptionSubtask13", Status.NEW, 2);

        taskManager.addSubtask(subtask11); //4
        taskManager.addSubtask(subtask12); //5
        taskManager.addSubtask(subtask13);//6

        taskManager.getSubtaskById(4);
        taskManager.getTaskById(0);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getEpicById(3);
        System.out.println(taskManager.getHistory());
        taskManager.removeHistory(3);
        System.out.println(taskManager.getHistory());

         */
    }
}
