package service;

import manager.FileBackedTasksManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

}