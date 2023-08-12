package tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasksId = new ArrayList<>();
    private final List<Subtask> subtasks = new ArrayList<>();

    private Instant endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        typeTask = TypeTask.EPIC;
        startTime = null;
        endTime = null;
        duration = Duration.ZERO;
    }

    public Epic(String name, String description, int id) {
        this(name, description);
        this.id = id;
    }

    public void addSubtask(Subtask subtask) {
        subtasksId.add(subtask.getId());
        subtasks.add(subtask);
        if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
            startTime = subtask.getStartTime();
        }
        if (endTime == null || subtask.getEndTime().isAfter(endTime)) {
            endTime = subtask.getEndTime();
        }
        duration = Duration.ZERO;
        for (Subtask sub : subtasks) {
            duration = duration.plus(sub.getDuration());
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
        if (subtask != null) {
            subtasks.remove(subtask);
            subtasksId.remove(toRemove);
            if (subtasks.isEmpty()) {
                defaultTime();
            } else {
                recalculateTimes();
            }
        }
    }

    private void defaultTime() {
        startTime = null;
        endTime = null;
        duration = Duration.ZERO;
    }

    private void recalculateTimes() {
        startTime = null;
        endTime = null;
        duration = Duration.ZERO;
        for (Subtask sub : subtasks) {
            if (startTime == null || sub.getStartTime().isBefore(startTime)) {
                startTime = sub.getStartTime();
            }
            if (endTime == null || sub.getEndTime().isAfter(endTime)) {
                endTime = sub.getEndTime();
            }
            duration = duration.plus(sub.getDuration());
        }
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
    public Instant getStartTime() {
        return startTime;
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }
}