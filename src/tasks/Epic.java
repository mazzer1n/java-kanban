package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksId = new ArrayList<>();
    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, int id) {
        this(name, description);
        this.id = id;
    }

    public void addSubtask (int id) {
        subtasksId.add(id);
    }

}
