package tasktracker;

import java.time.Instant;
import java.time.LocalDateTime;

public class Task {
    private Long id;
    private String description;
    private TaskStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public Task() {}

    public Task (Long id, String description) {
        this.id = id;
        this.description = description;
        this.status = TaskStatus.TODO;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt();
    }

    public void markAsProgress() {
        this.status = TaskStatus.IN_PROGRESS;
        setUpdatedAt();
    }

    public void markAsDone() {
        this.status = TaskStatus.DONE;
        setUpdatedAt();
    }

    private void setUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "Task { " +
                "id: " + id +
                ", description: '" + description + '\'' +
                ", status: " + status +
                ", createdAt:" + createdAt +
                ", updatedAt: " + updatedAt +
                " }";
    }
}
