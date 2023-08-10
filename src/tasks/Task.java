package tasks;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class Task {
    private int value = 5;
    protected String name;
    protected Status status;
    protected String description;
    protected int id;
    protected TypeTask typeTask;
    protected Instant startTime = Instant.now();
    protected int duration = 0;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.status = status;
        this.description = description;
        typeTask = TypeTask.TASK;
    }

    public Task(String name, String description, Status status, int id) {
        this(name, description, status);
        this.id = id;
    }

    public Task(String name, String description, Status status, Instant startTime, int duration) {
        this(name, description, status);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, Status status, int id, Instant startTime, int duration) {
        this(name, description, status, startTime, duration);
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeTask getTypeTask() {
        return typeTask;
    }

    public Instant getEndTime() {
        if (startTime != null) {
            return startTime.plus(Duration.ofMinutes(duration));
        } else {
            return null;
        }
    }

    public Instant getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }


    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                '}';
    }
}
