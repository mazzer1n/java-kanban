import java.util.ArrayList;
import java.util.Iterator;


public class Epic extends Task {
    ArrayList<Subtask> subtasks;

    Epic(String name) {
        super(name);
        subtasks = new ArrayList<>();
    }

    void updateSubtask(Subtask subtask) {
        for (Subtask task : subtasks) {
            if (task == subtask) {
                if (task.status.equals("NEW")) {
                    task.status = "IN_PROGRESS";
                } else {
                    task.status = "DONE";
                }
            }
        }
    }

    void сheckProgress() {
        int n = 0;
        int p = 0;
        int d = 0;
        for (Subtask subtask : subtasks) {
            if (subtask.status.equals("NEW")) {
                ++n;
            } else if (subtask.status.equals("IN_PROGRESS")) {
                ++p;
            } else {
                ++d;
            }
        }
        if (n != subtasks.size() && d != subtasks.size()) {
            this.status = "IN_PROGRESS";
        } else if (d == subtasks.size()) {
            this.status = "DONE";
        }
    }

    boolean searchSubtask(Subtask subtask) {
        for (Subtask subtask1 : subtasks) {
            if(subtask1 == subtask) {
                return true;
            }
        }
        return false;
    }

    void deleteSubtaskById(int id) {
        Iterator<Subtask> iterator = subtasks.iterator();
        while (((Iterator<?>) iterator).hasNext()) {
            Subtask subtask = iterator.next();
            if (subtask.id == id) {
                iterator.remove(); // удаление элемента через итератор
            }
        }
    }


}
