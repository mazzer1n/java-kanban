package service;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private static TaskManager taskManager;
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

}