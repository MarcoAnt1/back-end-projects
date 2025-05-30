package tasktracker.command;

import tasktracker.exception.InvalidCommandException;
import tasktracker.cli.ParsedCommand;
import tasktracker.core.TaskService;

public class AddCommand implements Command {
    @Override
    public void execute(ParsedCommand parsedCommand, TaskService service) throws InvalidCommandException {
        String description = parsedCommand.description()
                .orElseThrow(() -> new InvalidCommandException("Description is required for ADD command."));
        var newTask = service.addTask(description);
        System.out.println("Task added with ID: " + newTask.getId());
    }

    @Override
    public boolean requiresSave() {
        return true;
    }
}
