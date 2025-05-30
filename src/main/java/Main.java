import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import tasktracker.TaskTracker;
import tasktracker.command.CommandDispatcher;
import tasktracker.repository.JsonFileTaskRepository;

public class Main {

    public static void main(String[] args) {
            TaskTracker.track();
    }
}