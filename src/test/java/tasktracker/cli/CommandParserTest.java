package tasktracker.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exception.InvalidCommandException;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    private CommandParser parser;

    @BeforeEach
    void setUp() {
        parser = new CommandParser();
    }

    @Test
    void shouldParseAddCommandWithDescription() throws InvalidCommandException {
        ParsedCommand command = parser.parse("ADD \"My new task\"");
        assertEquals("ADD", command.command());
        assertEquals("My new task", command.description().orElse(""));
        assertTrue(command.id().isEmpty());
    }

    @Test
    void shouldParseListCommandWithoutArguments() throws InvalidCommandException {
        ParsedCommand command = parser.parse("LIST");
        assertEquals("LIST", command.command());
        assertTrue(command.description().isEmpty());
        assertTrue(command.id().isEmpty());
    }

    @Test
    void shouldParseListCommandWithStatus() throws InvalidCommandException {
        ParsedCommand command = parser.parse("LIST DONE");
        assertEquals("LIST", command.command());
        assertEquals("DONE", command.description().orElse(""));
        assertTrue(command.id().isEmpty());
    }

    @Test
    void shouldParseUpdateCommandWithIdAndDescription() throws InvalidCommandException {
        ParsedCommand command = parser.parse("UPDATE 5 \"Updated description\"");
        assertEquals("UPDATE", command.command());
        assertEquals("Updated description", command.description().orElse(""));
        assertEquals(5L, command.id().orElse(0L));
    }

    @Test
    void shouldParseDeleteCommandWithId() throws InvalidCommandException {
        ParsedCommand command = parser.parse("DELETE 10");
        assertEquals("DELETE", command.command());
        assertTrue(command.description().isEmpty());
        assertEquals(10L, command.id().orElse(0L));
    }

    @Test
    void shouldParseMarkDoneCommandWithId() throws InvalidCommandException {
        ParsedCommand command = parser.parse("MARK-DONE 7");
        assertEquals("MARK-DONE", command.command());
        assertTrue(command.description().isEmpty());
        assertEquals(7L, command.id().orElse(0L));
    }

    @Test
    void shouldParseMarkInProgressCommandWithId() throws InvalidCommandException {
        ParsedCommand command = parser.parse("MARK-IN-PROGRESS 3");
        assertEquals("MARK-IN-PROGRESS", command.command());
        assertTrue(command.description().isEmpty());
        assertEquals(3L, command.id().orElse(0L));
    }

    @Test
    void shouldThrowExceptionForInvalidCommandFormat() {
        assertThrows(InvalidCommandException.class, () -> parser.parse("INVALID_COMMAND"));
        assertThrows(InvalidCommandException.class, () -> parser.parse("ADD")); // Missing description for ADD
        assertThrows(InvalidCommandException.class, () -> parser.parse("UPDATE invalid_id \"desc\""));
    }

    @Test
    void shouldHandleExtraSpaces() throws InvalidCommandException {
        ParsedCommand command = parser.parse("  ADD   \"Task with spaces\"  ");
        assertEquals("ADD", command.command());
        assertEquals("Task with spaces", command.description().orElse(""));
    }

    @Test
    void shouldHandleDescriptionWithQuotesInside() {
        assertThrows(InvalidCommandException.class, ()-> parser.parse("ADD \"My \"quoted\" task\""));
    }

    @Test
    void shouldHandleMissingClosingQuote() {
        assertThrows(InvalidCommandException.class, () -> parser.parse("ADD \"Task missing quote"));
    }
}