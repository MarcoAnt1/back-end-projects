package tasktracker.command;

import tasktracker.exception.InvalidCommandException;
import tasktracker.cli.ParsedCommand;
import tasktracker.core.TaskService;

public class UpdateCommand implements Command {
    @Override
    public void execute(ParsedCommand parsedCommand, TaskService service) throws InvalidCommandException {
        Long id = parsedCommand.id()
                .orElseThrow(() -> new InvalidCommandException("ID is required for UPDATE command."));
        String description = parsedCommand.description()
                .orElseThrow(() -> new InvalidCommandException("Description is required for UPDATE command."));
        service.updateTaskDescription(id, description);
        System.out.println("Task ID " + id + " description updated.");
    }

    @Override
    public boolean requiresSave() {
        return true;
    }
}
