package manager;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager() {
        return  new FileBackedTasksManager("/Users/maksimmalyarov/IdeaProjects/java-kanban1/tasks.txt");
    }
}

