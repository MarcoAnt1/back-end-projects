package tasktracker.core;

import tasktracker.exception.TaskNotFoundException;
import tasktracker.repository.TaskRepository;

import java.io.IOException;
import java.util.List;

public class TaskService {
    private Tasks tasks;
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        loadTasks();
    }

    public void saveTasks() {
        try {
            taskRepository.saveTasks(tasks);
        } catch (IOException e) {
            System.err.println("Failed to save tasks: " + e.getMessage());
        }
    }

    public Task addTask(String description) {
        var newTask = tasks.add(description);
        saveTasks();
        return newTask;
    }

    public void updateTaskDescription(Long id, String description) throws TaskNotFoundException {
        tasks.updateDescription(id, description);
        saveTasks();
    }

    public void updateTaskStatus(Long id, boolean done) throws TaskNotFoundException {
        tasks.updateStatus(id, done);
        saveTasks();
    }

    public void deleteTask(Long id) throws TaskNotFoundException {
        tasks.delete(id);
    }

    public List<Task> getAllTasks() {
        return tasks.getAllTasks();
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return tasks.getTasksByStatus(status);
    }

    private void loadTasks() {
        try {
            this.tasks = taskRepository.loadTasks();
        } catch (IOException e) {
            this.tasks = new Tasks();
        } finally {
            this.tasks.synchronizeNextUniqueId();
        }
    }
}
