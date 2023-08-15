package service;

import exception.ManagerSaveException;
import exception.ManagerUpdateException;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static service.EpicTest.taskManager;


public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() throws IOException {
        taskManager = createTaskManager();
    }

    protected Task createTask() {
        return new Task("name", "description", Status.NEW);
    }

    @Test
    void shouldGetTaskByIdWithStandardBehavior() {
        assertNull(taskManager.getTaskById(2));
        Task task = createTask();
        taskManager.addTask(task);
        assertNotNull(taskManager.getTaskById(1));
    }

    @Test
    void shouldGetTaskByIdWithEmptyTaskList() {
        taskManager.clearTasks();
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    void shouldGetTaskByIdWithInvalidTaskId() {
        assertNull(taskManager.getTaskById(0));
        assertNull(taskManager.getTaskById(-1));
    }

    @Test
    void shouldAddTaskWithEmptyTaskList() {
        Task task = new Task("name1", "description1", Status.NEW, Instant.now(), Duration.ofSeconds(1));
        Task task1 = new Task("name2", "description2", Status.NEW,
                Instant.now().plusSeconds(10), Duration.ofSeconds(1));
        taskManager.clearTasks();

        taskManager.addTask(task);
        taskManager.addTask(task1);

        taskManager.clearTasks();
        taskManager.addTask(task);

        Task result = taskManager.getTaskById(task.getId());
        assertNotNull(result);
        assertEquals(task, result);
    }

    @Test
    void shouldUpdateTaskWithStandardBehavior() {
        Task task = new Task("name1", "description1", Status.NEW, Instant.now(), Duration.ofSeconds(1));
        Task updatedTask = new Task("name2", "description2", Status.NEW,
                Instant.now().plusSeconds(10), Duration.ofSeconds(1));
        updatedTask.setId(1);
        updatedTask.setName("newName");
        updatedTask.setDescription("newDescription");
        updatedTask.setStatus(Status.IN_PROGRESS);

        taskManager.addTask(task);
        taskManager.updateTask(updatedTask);

        Task result = taskManager.getTaskById(1);

        assertEquals(updatedTask.getName(), result.getName());
        assertEquals(updatedTask.getStatus(), result.getStatus());
        assertEquals(updatedTask.getDescription(), result.getDescription());
    }

    @Test
    void shouldUpdateTaskWithInvalidTask() {
        Task task = new Task("name1", "description1", Status.NEW, Instant.now(), Duration.ofSeconds(1));
        Task invalidTask = new Task("name2", "description2", Status.NEW,
                Instant.now().plusSeconds(10), Duration.ofSeconds(1));
        taskManager.addTask(task);
        invalidTask.setId(2);

        ManagerUpdateException thrown = assertThrows(ManagerUpdateException.class,
                () -> {
                    taskManager.updateTask(invalidTask);
                });
        assertTrue(thrown.getMessage().contains("Invalid task or tasks list is empty"));
    }


    @Test
    void shouldUpdateTaskWithEmptyTaskList() {
        ManagerUpdateException thrown = assertThrows(ManagerUpdateException.class,
                () -> {
                    taskManager.updateTask(createTask());
                });
        assertTrue(thrown.getMessage().contains("Invalid task or tasks list is empty"));
    }


    @Test
    void shouldDeleteTaskByIdWithStandardBehavior() {
        Task task = createTask();
        taskManager.addTask(task);

        taskManager.deleteTaskById(1);
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    void shouldDeleteTaskByIdWithEmptyTaskList() {
        taskManager.clearTasks();
        taskManager.deleteTaskById(1);
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    void shouldDeleteTaskByIdWithInvalidTaskId() {
        taskManager.deleteTaskById(0);
        taskManager.deleteTaskById(-1);
        assertNull(taskManager.getTaskById(0));
        assertNull(taskManager.getTaskById(-1));
    }

    @Test
    void shouldGetTaskListWithStandardBehavior() {
        Task task = new Task("name1", "description1", Status.NEW, Instant.now(), Duration.ofSeconds(1));
        Task task1 = new Task("name2", "description2", Status.NEW,
                Instant.now().plusSeconds(10), Duration.ofSeconds(1));
        task1.setName("newTask");

        taskManager.addTask(task);
        taskManager.addTask(task1);

        List<Task> tasks = taskManager.getTaskList();
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task));
        assertTrue(tasks.contains(task1));
    }

    @Test
    void shouldGetTaskListWithEmptyTaskList() {
        taskManager.clearTasks();
        List<Task> tasks = taskManager.getTaskList();
        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }

    @Test
    void shouldGetSubtasksOfEpicWithStandardBehavior() {
        Epic epic = new Epic("name", "description");
        Subtask subtask = new Subtask("nameSub", "description", Status.NEW, 1, Instant.now(), Duration.ofMinutes(5));
        Subtask subtask1 = new Subtask("nameSub1", "description", Status.NEW, 1,
                Instant.now().plusSeconds(400), Duration.ofMinutes(5));

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask1);

        List<Subtask> result = taskManager.getSubtasksOfEpic(epic);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(subtask));
        assertTrue(result.contains(subtask1));
    }

    @Test
    void shouldGetSubtasksOfEpicWithEmptyTaskList() {
        Epic epic = new Epic("name", "description");
        taskManager.clearTasks();
        List<Subtask> result = taskManager.getSubtasksOfEpic(epic);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldClearTasksWithStandardBehavior() {
        Task task = createTask();
        taskManager.addTask(task);

        taskManager.clearTasks();

        assertNull(taskManager.getTaskById(1));
    }

    @Test
    void shouldClearTasksWithEmptyTaskList() {
        taskManager.clearTasks();
        taskManager.clearTasks();
        assertNull(taskManager.getTaskById(1));
    }

    @Test
    void shouldClearTasksWithInvalidTaskId() {
        taskManager.clearTasks();
        taskManager.clearTasks();
        assertNull(taskManager.getTaskById(0));
        assertNull(taskManager.getTaskById(-1));
    }

    @Test
    void shouldReturnPrioritizedTasks() {
        List<Task> tasks;
        Task firstTask = new Task("name1", "description", Status.NEW, Instant.now(), Duration.ofMinutes(5));
        Task secondTask = new Task("name2", "description", Status.NEW,
                Instant.now().plus(Duration.ofDays(5)), Duration.ofMinutes(5));
        taskManager.addTask(secondTask);
        taskManager.addTask(firstTask);
        tasks = taskManager.getPrioritizedTasks();
        assertEquals(firstTask, tasks.get(0));
        assertEquals(secondTask, tasks.get(1));

    }

    @Test
    void shouldThrowExceptionTaskOverlaps() {
        Task firstTask = new Task("name1", "description", Status.NEW, Instant.now(), Duration.ofMinutes(5));
        Task secondTask = new Task("name2", "description", Status.NEW,
                Instant.now().plusSeconds(299), Duration.ofMinutes(5));

        taskManager.addTask(firstTask);
        ManagerSaveException thrown = assertThrows(ManagerSaveException.class,
                () -> {
                    taskManager.addTask(secondTask);
                });
        assertTrue(thrown.getMessage().contains("Tasks is overlaps"));
    }

}


