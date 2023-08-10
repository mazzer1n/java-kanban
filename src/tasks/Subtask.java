package tasks;

import java.time.Instant;

public class Subtask extends Task {
    private int epicId;


    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
        typeTask = TypeTask.SUBTASK;
    }

    public Subtask(String name, String description, Status status, int epicId, int id) {
        this(name, description, status, epicId);
        this.id = id;
    }

    public Subtask(String name, String description, Status status, int epicId, Instant startTime, int duration) {
        this(name, description, status, epicId);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Subtask(String name, String description, Status status, int epicId, int id, Instant startTime, int duration) {
        this(name, description, status, epicId, startTime, duration);
        this.id = id;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + epicId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Subtask other = (Subtask) obj;
        return epicId == other.epicId;
    }

    public int getDuration() {
        return this.duration;
    }

}

