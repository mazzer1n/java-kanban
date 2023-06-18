package service;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        
    }

    @Test
    void shouldGetAllTask() {
        Task task = createTask();
        Epic epic = new Epic("name", "description");
        Subtask subtask = new Subtask("nameSub", "description", Status.NEW,1);
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