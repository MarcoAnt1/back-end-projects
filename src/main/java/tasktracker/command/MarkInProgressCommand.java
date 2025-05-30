package tasktracker.command;

import tasktracker.exception.InvalidCommandException;
import tasktracker.cli.ParsedCommand;
import tasktracker.core.TaskService;

public class MarkInProgressCommand implements Command {
    @Override
    public void execute(ParsedCommand parsedCommand, TaskService service) throws InvalidCommandException {
        Long id = parsedCommand.id()
                .orElseThrow(() -> new InvalidCommandException("ID is required for MARK-IN-PROGRESS command."));
        service.updateTaskStatus(id, false);
        System.out.println("Task ID " + id + " marked in progress.");
    }

    @Override
    public boolean requiresSave() {
        return true;
    }
}
