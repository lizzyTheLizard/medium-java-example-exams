package lizzy.medium.security.exams.domain;

import java.util.List;

public interface SolutionAttemptRepository {
    int countForExamAndUser(Exam exam, String userId);

    List<SolutionAttempt> findForExam(Exam exam);

    void add(Exam exam, SolutionAttempt solutionAttempt);
}
