package service;

import manager.FileBackedTasksManager;
import manager.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//id,type,name,status,description,epic
//2,TASK,name,NEW,description
//1,EPIC,name,NEW,description
//3,SUBTASK,nameSub,NEW,description,1

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final File file = new File("/Users/maksimmalyarov/IdeaProjects/java-kanban1/tasks.txt");
    FileBackedTasksManager fileBackedTasksManager;

    @Override
    protected FileBackedTasksManager createTaskManager() {
        return new FileBackedTasksManager(file);
    }

    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        fileBackedTasksManager = createTaskManager();
    }

    @Test
    void shouldSaveChange() {
        Task task = createTask();
        Epic epic = new Epic("name", "description");
        Subtask subtask = new Subtask("nameSub", "description", Status.NEW, 1);

        fileBackedTasksManager.addEpic(epic);
        fileBackedTasksManager.addTask(task);
        fileBackedTasksManager.addSubtask(subtask);

        Task loadedTask = fileBackedTasksManager.getTaskById(task.getId());
        Epic loadedEpic = fileBackedTasksManager.getEpicById(epic.getId());
        Subtask loadedSubtask = fileBackedTasksManager.getSubtaskById(subtask.getId());

        assertEquals(task, loadedTask);
        assertEquals(epic, loadedEpic);
        assertEquals(subtask, loadedSubtask);

        loadedTask.setStatus(Status.IN_PROGRESS);
        loadedEpic.setStatus(Status.IN_PROGRESS);
        loadedSubtask.setStatus(Status.IN_PROGRESS);

        fileBackedTasksManager.updateTask(loadedTask);
        fileBackedTasksManager.updateEpic(loadedEpic);
        fileBackedTasksManager.updateSubtask(loadedSubtask);

        FileBackedTasksManager manager2 = TaskService.loadFromFile(file);

        Task updatedTask = manager2.getTaskById(task.getId());
        Epic updatedEpic = manager2.getEpicById(epic.getId());
        Subtask updatedSubtask = manager2.getSubtaskById(subtask.getId());

        assertEquals(loadedTask, updatedTask);
        assertEquals(loadedEpic, updatedEpic);
        assertEquals(loadedSubtask, updatedSubtask);
    }

    @Test
    void shouldGetAllTask() {
        Task task = createTask();
        Epic epic = new Epic("name", "description");
        Subtask subtask = new Subtask("nameSub", "description", Status.NEW, 1);

        fileBackedTasksManager.addEpic(epic);
        fileBackedTasksManager.addTask(task);
        fileBackedTasksManager.addSubtask(subtask);

        List<Task> result = fileBackedTasksManager.getAllTask();

        assertNotNull(result);
        assertTrue(result.contains(task));
        assertTrue(result.contains(epic));
        assertTrue(result.contains(subtask));
    }




}

