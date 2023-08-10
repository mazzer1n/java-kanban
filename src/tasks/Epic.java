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
        startTime = null;
    }

    public Epic(String name, String description, int id) {
        this(name, description);
        this.id = id;
        startTime = null;
    }

    public void addSubtask(Subtask subtask) {
        subtasksId.add(subtask.getId());
        subtasks.add(subtask);
        if (startTime == null) {
            startTime = subtask.getStartTime();
        }
        if (this.startTime.isAfter(subtask.getStartTime())) {
            this.startTime = subtask.getStartTime();
        }
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
        if (subtasks.isEmpty()) {
            defaultTime();
        }
    }

    private void defaultTime() {
        startTime = null;
        duration = 0;
    }

    public void clearSubtasksId() {
        subtasksId.clear();
        subtasks.clear();
        defaultTime();
    }


    public List<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public Instant getEndTime() {
        if (startTime != null) {
            Instant endTime = startTime;
            for (Subtask subtask : subtasks) {
                if (endTime.isBefore(subtask.getEndTime())) {
                    endTime = subtask.getEndTime();
                }
            }
            return endTime;
        }
        return null;
    }

    @Override
    public Instant getStartTime() {
        return startTime;
    }

}
