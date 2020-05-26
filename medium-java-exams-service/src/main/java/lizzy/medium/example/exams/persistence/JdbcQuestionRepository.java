package lizzy.medium.example.exams.persistence;

import lizzy.medium.example.exams.domain.model.Exam;
import lizzy.medium.example.exams.domain.model.Question;
import lizzy.medium.example.exams.domain.model.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JdbcQuestionRepository implements QuestionRepository {
    private final JdbcQuestionSpringDataRepository jdbcQuestionSpringDataRepository;

    @Override
    public List<Question> getQuestions(Exam exam) {
        return jdbcQuestionSpringDataRepository.findByExam(JdbcExam.of(exam)).stream()
                .map(JdbcQuestion::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void add(Exam exam, Question question) {
        JdbcQuestion jdbcQuestion = JdbcQuestion.of(question, exam);
        jdbcQuestionSpringDataRepository.save(jdbcQuestion);
    }

}

