package tasktracker.repository;

import tasktracker.core.Tasks;

import java.io.IOException;

public interface TaskRepository {
    Tasks loadTasks() throws IOException;
    void saveTasks(Tasks tasks) throws IOException;
}
