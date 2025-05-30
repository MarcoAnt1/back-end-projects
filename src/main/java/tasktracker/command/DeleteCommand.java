package tasktracker.command;

import tasktracker.exception.InvalidCommandException;
import tasktracker.cli.ParsedCommand;
import tasktracker.core.TaskService;

public class DeleteCommand implements Command {
    @Override
    public void execute(ParsedCommand parsedCommand, TaskService service) throws InvalidCommandException {
        Long id = parsedCommand.id().orElseThrow(() -> new InvalidCommandException("ID is required for UPDATE command."));
        service.deleteTask(id);
        System.out.println("Task ID " + id + " deleted.");
    }

    @Override
    public boolean requiresSave() {
        return true;
    }
}
