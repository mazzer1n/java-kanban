package Tasks;

public class Task {
    protected String name;
    protected String status;
    protected String description;
    protected int id;



   public Task (String name, String description) {
        this.name = name;
        this.status = "NEW";
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
