

public class Task {
    protected String name;
    public String status;
    public int id;



    Task (String name) {
        this.name = name;
        this.status = "NEW";

    }

    void updateTask() {
        if (status.equals("NEW")) {
            status = "IN_PROGRESS";
        } else {
            status = "DONE";
        }
    }


}
