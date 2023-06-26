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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest {

    private File file;
    private FileBackedTasksManager fileBackedTasksManager;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("tasks", ".txt");
        fileBackedTasksManager = new FileBackedTasksManager(file);
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

        FileBackedTasksManager restoredManager = TaskService.loadFromFile(file);

        Task updatedTask = restoredManager.getTaskById(task.getId());
        Epic updatedEpic = restoredManager.getEpicById(epic.getId());
        Subtask updatedSubtask = restoredManager.getSubtaskById(subtask.getId());

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

        List<Task> result = fileBackedTasksManager.getAllTasks();

        assertNotNull(result);
        assertTrue(result.contains(task));
        assertTrue(result.contains(epic));
        assertTrue(result.contains(subtask));
    }

    private Task createTask() {
        return new Task("name", "description", Status.NEW);
    }

    @Test
    void shouldRestoreEmptyTaskList() throws IOException {

        FileBackedTasksManager restoredManager = TaskService.loadFromFile(file);

        assertEquals(0, restoredManager.getNextId());
        assertTrue(restoredManager.getTaskList().isEmpty());
        assertTrue(restoredManager.getEpicList().isEmpty());
        assertTrue(restoredManager.getSubtaskList().isEmpty());
        assertTrue(restoredManager.getHistory().isEmpty());
    }



}


