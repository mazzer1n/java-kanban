package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksId = new ArrayList<>();
    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, int id) {
        this(name, description);
        this.id = id;
    }

    public void addSubtask(int id) {
        subtasksId.add(id);
    }

    public void deleteSubtaskId(int id) {
        Integer toRemove = id;
        subtasksId.remove(toRemove);
    }

    public void clearSubtasksId() {
        subtasksId.clear();
    }

    public List<Integer> getSubtasksId() {
        return subtasksId;
    }
}
