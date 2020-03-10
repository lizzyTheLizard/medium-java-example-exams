package lizzy.medium.security.exams.persistence;

import lizzy.medium.security.exams.domain.*;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JdbcExamRepository implements ExamRepository {
    private final JdbcExamSpringDataRepository jdbcExamSpringDataRepository;
    private final ExamFactory examFactory;

    JdbcExamRepository(JdbcExamSpringDataRepository jdbcExamSpringDataRepository,
            SolutionAttemptRepository solutionAttemptRepository,
            QuestionRepository questionRepository){
        //Cannot inject exam factory as this would be a circual dependency
        this.examFactory = new ExamFactory(solutionAttemptRepository, questionRepository, this);
        this.jdbcExamSpringDataRepository = jdbcExamSpringDataRepository;
    }

    @Override
    public List<Exam> findAllOwnedBy(String userId) {
        return jdbcExamSpringDataRepository.findByOwner(userId).stream()
                .map(e -> e.toAggregate(examFactory))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<Exam> findById(UUID examId) {
        return jdbcExamSpringDataRepository.findById(examId)
                .map(e -> e.toAggregate(examFactory));
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

