package lizzy.medium.example.exams.domain.model;

import java.util.List;

public interface ParticipationRepository {
    int getNumberOfFailed(Exam exam, User user);

    List<Participation> getMostRecentForeachUser(Exam exam);

    void add(Exam exam, Participation participation);
}
