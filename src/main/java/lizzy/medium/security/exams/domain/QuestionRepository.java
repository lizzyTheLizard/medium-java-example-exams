package lizzy.medium.security.exams.domain;

import java.util.List;

public interface QuestionRepository {
    List<Question> findForExam(Exam exam);

    void add(Exam exam, Question question);
}
