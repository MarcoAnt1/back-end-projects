package tasktracker.command;

import tasktracker.cli.ParsedCommand;
import tasktracker.core.Task;
import tasktracker.core.TaskService;
import tasktracker.core.TaskStatus;
import tasktracker.exception.InvalidCommandException;

import java.util.List;
import java.util.Optional;

public class ListCommand implements Command {
    @Override
    public void execute(ParsedCommand parsedCommand, TaskService service) throws InvalidCommandException {
        Optional<String> statusString = parsedCommand.description();

        List<Task> tasksToDisplay;
        if (statusString.isEmpty() || statusString.get().equalsIgnoreCase("ALL")) {
            tasksToDisplay = service.getAllTasks();
        } else {
            try {
                TaskStatus status = TaskStatus.valueOf(statusString.get().toUpperCase());
                tasksToDisplay = service.getTasksByStatus(status);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid status for LIST command. Use ALL, DONE, or IN_PROGRESS.");
                tasksToDisplay = List.of();
            }
        }

        if (tasksToDisplay.isEmpty()) {
            System.out.println("No tasks to show for this filter.");
        } else {
            tasksToDisplay.forEach(System.out::println);
        }
    }

    @Override
    public boolean requiresSave() {
        return false;
    }
}
