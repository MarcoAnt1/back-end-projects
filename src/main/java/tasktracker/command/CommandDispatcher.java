package tasktracker.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandDispatcher {
    private final Map<String, Command> commandMap;

    public CommandDispatcher() {
        commandMap = new HashMap<>();

        registerCommand("ADD", new AddCommand());
        registerCommand("UPDATE", new UpdateCommand());
        registerCommand("DELETE", new DeleteCommand());
        registerCommand("MARK-IN-PROGRESS", new MarkInProgressCommand());
        registerCommand("MARK-DONE", new MarkDoneCommand());
        registerCommand("LIST", new ListCommand());
    }

    private void registerCommand(String commandName, Command command) {
        commandMap.put(commandName.toUpperCase(), command);
    }

    public Optional<Command> getCommandMap(String commandName) {
        return Optional.ofNullable(commandMap.get(commandName.toUpperCase()));
    }
}
