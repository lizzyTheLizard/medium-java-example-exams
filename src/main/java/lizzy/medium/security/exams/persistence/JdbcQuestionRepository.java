package lizzy.medium.security.exams.persistence;

import lizzy.medium.security.exams.domain.Exam;
import lizzy.medium.security.exams.domain.Question;
import lizzy.medium.security.exams.domain.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JdbcQuestionRepository implements QuestionRepository {
    private final JdbcQuestionSpringDataRepository jdbcQuestionSpringDataRepository;

    @Override
    public List<Question> findForExam(Exam exam) {
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

