package tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
        this.typeTask = TypeTask.SUBTASK;
    }

    public Subtask(String name, String description, Status status, int epicId, int id) {
        this(name, description, status, epicId);
        this.id = id;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}
