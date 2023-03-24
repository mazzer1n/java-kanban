package tasks;

public class Epic extends Task {
    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, int id) {
        this(name, description);
        this.id = id;
    }

}
