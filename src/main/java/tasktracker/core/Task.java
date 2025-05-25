package tasktracker.core;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Task {
    private final Long id;
    private String description;
    private TaskStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public Task() {
        this.id = null;
        this.description = null;
        this.status = null;
        this.createdAt = null;
        this.updatedAt = null;
    }

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
        if (description != null && !description.trim().isEmpty()) { // Add basic validation
            this.description = description;
            updateTimestamp();
        } else {
            System.err.println("Description cannot be empty for task " + this.id); // Or throw an IllegalArgumentException
        }
    }

    public void markAsProgress() {
        if (this.status != TaskStatus.IN_PROGRESS) {
            this.status = TaskStatus.IN_PROGRESS;
            updateTimestamp();
        }
    }

    public void markAsDone() {
        if (this.status != TaskStatus.DONE) {
            this.status = TaskStatus.DONE;
            updateTimestamp();
        }
    }

    @Override
    public String toString() {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault()); // Use system's default time zone

        return "Task { " +
                "id: " + id +
                ", description: '" + description + '\'' +
                ", status: " + status +
                ", createdAt: " + formatter.format(createdAt) +
                ", updatedAt: " + formatter.format(updatedAt) +
                " }";
    }

    private void updateTimestamp() {
        this.updatedAt = Instant.now();
    }
}
