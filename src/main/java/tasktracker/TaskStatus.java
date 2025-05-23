package tasktracker;

public enum TaskStatus {
    TODO("todo"),
    IN_PROGRESS("in-progress"),
    DONE("done");

    private String description;

    TaskStatus(final String description) {
        this.description = description;
    }
}
