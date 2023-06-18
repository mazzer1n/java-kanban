package tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksId = new ArrayList<>();
    private final List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        typeTask = TypeTask.EPIC;
    }

    public Epic(String name, String description, int id) {
        this(name, description);
        this.id = id;
    }

    public void addSubtask(Subtask subtask) {
        subtasksId.add(subtask.getId());
        subtasks.add(subtask);
    }

    public void deleteSubtaskId(int id) {
        Integer toRemove = id;
        Subtask subtask = null;
        for (Subtask sub : subtasks) {
            if (sub.getId() == id) {
                subtask = sub;
                break;
            }
        }
        subtasks.remove(subtask);
        subtasksId.remove(toRemove);
    }

    public void clearSubtasksId() {
        subtasksId.clear();
        subtasks.clear();
    }


    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public Instant getEndTime() {
        Instant endTime = startTime;
        for (Subtask subtask : subtasks) {
            endTime = endTime.plus(Duration.ofMinutes(subtask.getDuration()));
        }
        return endTime;
    }

}
