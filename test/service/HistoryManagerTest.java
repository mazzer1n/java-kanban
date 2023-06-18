package service;

import static org.junit.jupiter.api.Assertions.*;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;
import java.util.List;

//Тут почему то не проходят тесты
class HistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddTaskToHistory() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task));
    }

    @Test
    void shouldNotAddNullTaskToHistory() {
        Task task = null;
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertFalse(history.contains(task1));
        assertTrue(history.contains(task2));
    }

    @Test
    void shouldReturnEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldHandleDuplicateTasksInHistory() {
        Task task = new Task("Task 1", "Description 1", Status.NEW);
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task));
    }

    @Test
    void shouldRemoveTaskFromBeginningOfHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertFalse(history.contains(task1));
        assertTrue(history.contains(task2));
    }

    @Test
    void shouldRemoveTaskFromMiddleOfHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);
        Task task3 = new Task("Task 3", "Description 3", Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertTrue(history.contains(task1));
        assertFalse(history.contains(task2));
        assertTrue(history.contains(task3));
    }

    @Test
    void shouldRemoveTaskFromEndOfHistory() {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW);
        Task task2 = new Task("Task 2", "Description 2", Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task1));
        assertFalse(history.contains(task2));
    }
}
