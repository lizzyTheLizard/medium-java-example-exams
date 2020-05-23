package lizzy.medium.example.exams.domain.model;

import java.util.List;

public interface QuestionRepository {
    List<Question> getQuestions(Exam exam);

    void add(Exam exam, Question question);
}
