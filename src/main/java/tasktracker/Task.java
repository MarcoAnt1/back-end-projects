package tasktracker;

import java.time.Instant;

public class Task {
    private Long id;
    private String description;
    private TaskStatus status;
    private Instant createdAt;
    private Instant updatedAt;

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

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
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
    }

    public void markAsDone() {
        this.status = TaskStatus.DONE;
    }

    private void setUpdatedAt() {
        this.updatedAt = Instant.now();
    }
}
