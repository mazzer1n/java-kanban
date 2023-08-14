package tasks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasksId = new ArrayList<>();
    private Instant endTime = null;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        typeTask = TypeTask.EPIC;
        startTime = null;
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

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return getId() == epic.getId() &&
                Objects.equals(getName(), epic.getName()) &&
                getStatus() == epic.getStatus() &&
                Objects.equals(getDescription(), epic.getDescription());
    }


    @Override
    public int hashCode() {
        return Objects.hash(subtasksId, endTime);
    }
}
