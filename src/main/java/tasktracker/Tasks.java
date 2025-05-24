package tasktracker;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Tasks {
    public final List<Task> tasks;
    private AtomicLong nextUniqueId;

    public Tasks() {
        tasks = new ArrayList<>();
        nextUniqueId = new AtomicLong(1L);
    }

    public void synchronizeNextUniqueId() {
        Optional<Long> maxId = tasks.stream().map(Task::getId).max(Long::compareTo);
        nextUniqueId.set(maxId.map(id -> id + 1).orElse(1L));
    }

    public Task add(String description) {
        var newTask = new Task(nextUniqueId.getAndIncrement(), description);
        tasks.add(newTask);
        return newTask;
    }

    public void updateDescription(Long id, String description) {
        Optional<Task> task = findTaskById(id);

        if (task.isPresent()) {
            task.get().setDescription(description);
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    public void updateStatus(Long id, boolean done) {
        Optional<Task> task = findTaskById(id);

        if (task.isPresent()) {
            if (done) {
                task.get().markAsDone();
            } else {
                task.get().markAsProgress();
            }
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    public boolean delete(Long id) {
        boolean removed = tasks.removeIf(t -> t.getId().equals(id));
        if (!removed) {
            System.out.println("Task with ID " + id + " not found for deletion.");
        }
        return removed;
    }

    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return tasks.stream().filter(task -> task.getStatus().equals(status)).collect(Collectors.toList());
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setNextUniqueId(Long id) {
        this.nextUniqueId = new AtomicLong(id);
    }

    public long getNextUniqueId() {
        return nextUniqueId.get();
    }

    private Optional<Task> findTaskById(Long id) {
        return tasks.stream().filter(task -> task.getId().equals(id)).findFirst();
    }
}
