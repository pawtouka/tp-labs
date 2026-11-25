package by.planner.model;

import java.io.IOException;
import java.util.List;

public interface TaskRepository {
    List<StudyTask> load() throws IOException;
    void save(List<StudyTask> tasks) throws IOException;
}
