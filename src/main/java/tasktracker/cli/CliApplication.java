package tasktracker.cli;

import tasktracker.command.Command;
import tasktracker.command.CommandDispatcher;
import tasktracker.core.TaskService;
import tasktracker.exception.InvalidCommandException;
import tasktracker.exception.TaskNotFoundException;

import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public class CliApplication {
    private final CommandParser commandParser;
    private final CommandDispatcher commandDispatcher;
    private final TaskService taskService;

    public CliApplication(CommandParser commandParser, CommandDispatcher commandDispatcher, TaskService taskService) {
        this.commandParser = commandParser;
        this.commandDispatcher = commandDispatcher;
        this.taskService = taskService;
    }

    public void run() {
        System.out.println("Start Task Tracker. Type 'exit' to quit.");
        System.out.println("Commands: ADD \"desc\", UPDATE <id> \"desc\", DELETE <id>, MARK-IN-PROGRESS <id>, MARK-DONE <id>, LIST [ALL|DONE|IN_PROGRESS]");

        try (var scanner = new Scanner(System.in)) {
            String input;

            while (true) {
                System.out.print(">");
                input = scanner.nextLine();

                if (Objects.equals(input.toLowerCase(), "exit")) {
                    System.out.println("Exiting Task Tracker. Goodbye!");
                    break;
                }

                try {
                    var parsedCommand = commandParser.parse(input);
                    String commandName = parsedCommand.command();

                    Optional<Command> commandOptional = commandDispatcher.getCommandMap(commandName);

                    if (commandOptional.isPresent()) {
                        var command = commandOptional.get();
                        command.execute(parsedCommand, taskService);
                    } else {
                        System.err.println("Unknown command: " + commandName);
                    }
                } catch (InvalidCommandException | TaskNotFoundException e) {
                    System.err.println("Error: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("An unexpected error occurred: " + e.getMessage());
                }
            }
        }
    }
}
