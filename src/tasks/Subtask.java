package tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, String status) {
        super(name, description, status);
    }

    public Subtask(String name, String description, String status, int id) {
        this(name, description, status);
        this.id = id;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}
