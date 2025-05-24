package tasktracker;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(long taskId) {
        super("Task with ID " + taskId + " not found.");
    }
}
