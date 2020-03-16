package lizzy.medium.example.exams.domain;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ExamRepository {
    Collection<Exam> findAllRunningOwnedBy(User user);

    Optional<Exam> findById(UUID examId);

    void update(Exam exam);

    void add(Exam exam);
}
