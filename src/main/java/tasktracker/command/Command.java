package tasktracker.command;

import tasktracker.exception.InvalidCommandException;
import tasktracker.cli.ParsedCommand;
import tasktracker.core.TaskService;

public interface Command {
    void execute(ParsedCommand parsedCommand, TaskService service) throws InvalidCommandException;
    boolean requiresSave();
}
