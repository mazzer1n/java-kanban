package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private final HashMap<Integer, Node> history = new HashMap<>();
    Node head;
    Node tail;

    @Override
    public void add(Task task) {
        if (task == null) return;
        remove(task.getId());
        linkLast(task);
    }


    @Override
    public void remove(int id) {
        Node node = history.remove(id);
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        final ArrayList<Task> list = new ArrayList<>();
        Node current = head;

        while (current != null) {
            list.add(current.item);
            current = current.next;
        }

        return list;
    }


    private void removeNode(Node node) {
        if (node == null)
            return;

        Node prev = node.prev;
        Node next = node.next;

        if (prev == null) {
            head = next;
            if (head != null) {
                head.prev = null;
            } else {
                tail = null;
            }
        } else {
            prev.next = next;
        }

        if (next == null) {
            tail = prev;
            if (tail != null) {
                tail.next = null;
            } else {
                head = null;
            }
        } else {
            next.prev = prev;
        }
    }


    private void linkLast(Task element) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, element, null);
        tail = newNode;

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }

        history.put(newNode.item.getId(), newNode);
    }

}
