package tasktracker.command;

import tasktracker.exception.InvalidCommandException;
import tasktracker.cli.ParsedCommand;
import tasktracker.core.TaskService;

public class MarkDoneCommand implements Command {
    @Override
    public void execute(ParsedCommand parsedCommand, TaskService service) throws InvalidCommandException {
        Long id = parsedCommand.id()
                .orElseThrow(() -> new InvalidCommandException("ID is required for MARK-DONE command."));
        service.updateTaskStatus(id, true);
        System.out.println("Task ID " + id + " marked done.");
    }

    @Override
    public boolean requiresSave() {
        return true;
    }
}
