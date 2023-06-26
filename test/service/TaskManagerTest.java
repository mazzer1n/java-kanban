package service;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static service.EpicTest.taskManager;


public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
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
        Task task = createTask();
        Task task1 = createTask();
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
        Task task = createTask();
        Task updatedTask = createTask();
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
    void shouldUpdateTaskWithInvalidTaskId() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = createTask();
        taskManager.addTask(task);
        Task invalidTask = createTask();
        invalidTask.setId(2);
        taskManager.addTask(invalidTask);
        Task updatedTask = createTask();
        updatedTask.setId(2);
        taskManager.updateTask(updatedTask);
        assertNotNull(taskManager.getTaskById(2));
        assertEquals(task, taskManager.getTaskById(1));
    }


    @Test
    void shouldUpdateTaskWithEmptyTaskList() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = createTask();
        taskManager.addTask(task);
        taskManager.clearTasks();
        task.setName("Updated Task");
        taskManager.addTask(task);
        assertNull(taskManager.getTaskById(1));
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
        Task task = createTask();
        Task task1 = createTask();
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
        Subtask subtask = new Subtask("nameSub", "description", Status.NEW, 1);
        Subtask subtask1 = new Subtask("nameSub1", "description", Status.NEW, 1);

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
}
