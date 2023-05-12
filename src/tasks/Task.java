package tasks;

public class Task {
    protected String name;
    protected Status status;
    protected String description;
    protected int id;
    protected TypeTask typeTask = TypeTask.TASK;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public Task(String name, String description, Status status, int id) {
        this(name, description, status);
        this.id = id;
    }

    public String getName() {
        return name;
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
