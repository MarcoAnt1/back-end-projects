package tasktracker.cli;

import tasktracker.exception.InvalidCommandException;

import java.util.Optional;
import java.util.regex.Pattern;

public class CommandParser {
    private static final Pattern COMMAND_PATTERN =
            Pattern.compile("^\\s*([A-Za-z-]+)(?:\\s+(\\d+|[A-Za-z_]+))?(?:\\s+\"([^\"]*)\")?\\s*$");

    public ParsedCommand parse(String input) throws InvalidCommandException {
        if (input == null || input.isEmpty()) {
            throw new InvalidCommandException("Input cannot be empty.");
        }

        var matcher = COMMAND_PATTERN.matcher(input);
        if (!matcher.matches()) {
            throw new InvalidCommandException("Malformed command. Expected format: COMMAND [ID/STATUS] [\"DESCRIPTION\"]");
        }

        String command = matcher.group(1).toUpperCase();
        String valueStr = matcher.group(2);
        String descriptionStr = matcher.group(3);

        Optional<Long> id = Optional.empty();
        Optional<String> description = Optional.ofNullable(descriptionStr);

        if (valueStr != null) {
            try {
                id = Optional.of(Long.parseLong(valueStr));
            } catch (NumberFormatException e) {
                if (!command.equals("LIST")) {
                    throw new InvalidCommandException("Expected a numeric ID for command '" + command + "', but got '" + valueStr + "'.");
                }

                description = Optional.of(valueStr);
                id = Optional.empty();
            }
        }

        if (command.equals("ADD") && description.isEmpty()) {
            throw new InvalidCommandException("ADD command requires a description (e.g., ADD \"Buy groceries\").");
        }

        if ((command.equals("UPDATE") || command.equals("DELETE") ||
                command.equals("MARK-IN-PROGRESS") || command.equals("MARK-DONE")) && id.isEmpty()) {
            throw new InvalidCommandException("Command '" + command + "' requires an ID (e.g., " + command + " 123).");
        }

        return new ParsedCommand(command, id, description);
    }
}
