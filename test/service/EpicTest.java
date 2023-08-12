package service;

import exception.ManagerSaveException;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;


import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    public static TaskManager taskManager;
    private static Epic epic;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        epic = new Epic("name", "description", 1);
        taskManager.addEpic(epic);
    }

    @Test
    public void shouldReturnStatusNewWithoutSubtasks() {
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnStatusNewWithSubtasks() {
        taskManager.addSubtask(new Subtask("name1", "description", Status.NEW, 1));
        taskManager.addSubtask(new Subtask("name2", "description", Status.NEW, 1));
        taskManager.addSubtask(new Subtask("name3", "description", Status.NEW, 1));
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void shouldReturnStatusDone() {
        taskManager.addSubtask(new Subtask("name1", "description", Status.DONE, 1));
        taskManager.addSubtask(new Subtask("name2", "description", Status.DONE, 1));
        taskManager.addSubtask(new Subtask("name3", "description", Status.DONE, 1));
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void shouldReturnStatusInProgressWithDifferent() {
        taskManager.addSubtask(new Subtask("name1", "description", Status.DONE, 1));
        taskManager.addSubtask(new Subtask("name2", "description", Status.NEW, 1));
        taskManager.addSubtask(new Subtask("name3", "description", Status.DONE, 1));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldReturnStatusInProgress() {
        taskManager.addSubtask(new Subtask("name1", "description", Status.IN_PROGRESS, 1));
        taskManager.addSubtask(new Subtask("name2", "description", Status.IN_PROGRESS, 1));
        taskManager.addSubtask(new Subtask("name3", "description", Status.IN_PROGRESS, 1));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void shouldReturnStartTime() {
        Subtask subtask1 = new Subtask("name1", "description", Status.NEW, 1, Instant.now(),
                Duration.ofMinutes(5));
        Subtask subtask2 = new Subtask("name2", "description", Status.NEW, 1,
                Instant.now().minusSeconds(350), Duration.ofMinutes(5));
        taskManager.addSubtask(subtask1);
        assertEquals(epic.getStartTime(), subtask1.getStartTime());
        taskManager.addSubtask(subtask2);
        assertEquals(epic.getStartTime(), subtask2.getStartTime());
        taskManager.deleteSubtaskById(2);
        taskManager.deleteSubtaskById(3);
        assertNull(epic.getStartTime());
    }

    @Test
    public void shouldReturnEndTime() {
        Subtask subtask1 = new Subtask("name1", "description", Status.NEW, 1, Instant.now(),
                Duration.ofMinutes(5));
        Subtask subtask2 = new Subtask("name2", "description", Status.NEW, 1,
                Instant.now().plusSeconds(350), Duration.ofMinutes(30));
        assertNull(epic.getEndTime());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(epic.getEndTime(), subtask2.getEndTime());
        taskManager.deleteSubtaskById(3);
        assertEquals(epic.getEndTime(), subtask1.getEndTime());
    }

    @Test
    public void shouldThrowExceptionTaskOverlaps() {
        Subtask subtask1 = new Subtask("name1", "description", Status.NEW, 1, Instant.now(),
                Duration.ofMinutes(5));
        Subtask subtask2 = new Subtask("name2", "description", Status.NEW, 1,
                Instant.now().plusSeconds(299), Duration.ofMinutes(5));
        taskManager.addSubtask(subtask1);
        ManagerSaveException thrown = assertThrows(ManagerSaveException.class,
                () -> {
                    taskManager.addSubtask(subtask2);
                });
        assertTrue(thrown.getMessage().contains("Task overlaps with existing tasks."));
    }

}