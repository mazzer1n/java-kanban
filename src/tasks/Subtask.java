package tasks;


import java.time.Duration;
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

    public Subtask(String name, String description, Status status, int epicId, int id , Instant startTime, Duration duration) {
        this(name, description, status, epicId,id);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Subtask(String name, String description, Status status, int epicId,Instant startTime, Duration duration) {
        this(name, description, status, epicId);
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}


