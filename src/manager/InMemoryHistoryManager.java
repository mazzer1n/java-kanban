package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {

    private static class Node<Task> {
        Task item;
        Node next;
        Node prev;

        Node(Node<Task> prev, Task element, Node<Task> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private final HashMap<Integer, Node> history = new HashMap<>();
    Node<Task> head;
    Node<Task> tail;

    @Override
    public void add(Task task) {
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
        ArrayList<Task> list = new ArrayList<>();
        Node<Task> current = head;

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

        if (prev == null)
            head = next;
         else
            prev.next = next;


        if (next == null)
            tail = prev;
         else
            next.prev = prev;

    }


    private void linkLast(Task element) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }

        history.put(newNode.item.getId(),newNode);
    }

}
