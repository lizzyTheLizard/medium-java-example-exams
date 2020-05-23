package lizzy.medium.example.exams.persistence;

import lizzy.medium.example.exams.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JdbcExamRepository implements ExamRepository {
    private final JdbcExamSpringDataRepository jdbcExamSpringDataRepository;

    JdbcExamRepository(JdbcExamSpringDataRepository jdbcExamSpringDataRepository,
                       ParticipationRepository participationRepository,
                       QuestionRepository questionRepository) {
        //Cannot inject exam factory as this would be a circular dependency
        this.jdbcExamSpringDataRepository = jdbcExamSpringDataRepository;
    }

    @Override
    public List<Exam> findAllRunningOwnedBy(User user) {
        return jdbcExamSpringDataRepository.findByOwnerAndClosed(user.getId(), false).stream()
                .map(e -> e.toAggregate())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<Exam> findById(UUID examId) {
        return jdbcExamSpringDataRepository.findById(examId)
                .map(e -> e.toAggregate());
    }

    @Override
    public void update(Exam exam) {
        JdbcExam jdbcExam = JdbcExam.of(exam);
        jdbcExamSpringDataRepository.save(jdbcExam);
    }

    @Override
    public void add(Exam exam) {
        JdbcExam jdbcExam = JdbcExam.of(exam);
        jdbcExamSpringDataRepository.save(jdbcExam);
    }
}

