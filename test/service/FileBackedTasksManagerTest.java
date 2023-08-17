package service;

import manager.FileBackedTasksManager;
import manager.InMemoryTaskManager;
import manager.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private final File file = File.createTempFile("tasks", ".txt");


    public FileBackedTasksManagerTest() throws IOException {
    }


    @Override
    protected FileBackedTasksManager createTaskManager() {
        return new FileBackedTasksManager(file.getAbsolutePath());
    }

    @Test
    void shouldRestoreEmptyTaskList() throws IOException {
        File file = createTempFile();
        FileBackedTasksManager manager = new FileBackedTasksManager(file.getAbsolutePath());

        manager.clearTasks();

        FileBackedTasksManager restoredManager = TaskService.loadFromFile(file.getAbsolutePath());

        assertTrue(restoredManager.getTaskList().isEmpty());
    }

    @Test
    void shouldRestoreEpicWithoutSubtasks() throws IOException {
        File file = createTempFile();
        FileBackedTasksManager manager = new FileBackedTasksManager(file.getAbsolutePath());

        Epic epic = new Epic("Epic without Subtasks", "Description");
        manager.addEpic(epic);

        FileBackedTasksManager restoredManager = TaskService.loadFromFile(file.getAbsolutePath());

        List<Epic> epics = restoredManager.getEpicList();
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));
        assertTrue(restoredManager.getSubtaskList().isEmpty());
    }

    @Test
    void shouldRestoreEmptyHistory() throws IOException {
        File file = createTempFile();
        FileBackedTasksManager manager = new FileBackedTasksManager(file.getAbsolutePath());

        Task task = new Task("Task", "Description", Status.NEW);
        manager.addTask(task);

        manager.clearHistory();

        FileBackedTasksManager restoredManager = TaskService.loadFromFile(file.getAbsolutePath());

        assertTrue(restoredManager.getHistory().isEmpty());
    }

    private File createTempFile() throws IOException {
        return File.createTempFile("testfile", ".txt");
    }


}


