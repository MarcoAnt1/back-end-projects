package tasktracker;

import java.util.Optional;

public record ParsedCommand(String command, Optional<Long> id, Optional<String> description) {
}
