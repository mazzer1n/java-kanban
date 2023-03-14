package tasks;

public class Epic extends Task {
    public Epic(String name, String description) {
        super(name, description, "NEW");
    }

    public Epic(String name, String description, String status, int id) {
        super(name, description, status);
        this.id = id;
    }

}
