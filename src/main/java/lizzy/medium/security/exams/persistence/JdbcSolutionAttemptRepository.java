package lizzy.medium.security.exams.persistence;

import lizzy.medium.security.exams.domain.Exam;
import lizzy.medium.security.exams.domain.SolutionAttempt;
import lizzy.medium.security.exams.domain.SolutionAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JdbcSolutionAttemptRepository implements SolutionAttemptRepository {
    private final JdbcSolutionAttemptSpringDataRepository springDataSolutionAttemptRepository;

    @Override
    public int countForExamAndUser(Exam exam, String userId) {
        JdbcExam jdbcExam = JdbcExam.of(exam);
        return springDataSolutionAttemptRepository.countByExamAndUserId(jdbcExam, userId);
    }

    @Override
    public List<SolutionAttempt> findForExam(Exam exam) {
        JdbcExam jdbcExam = JdbcExam.of(exam);
        return springDataSolutionAttemptRepository.findByExam(jdbcExam).stream()
                .map(JdbcSolutionAttempt::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void add(Exam exam, SolutionAttempt solutionAttempt) {
        JdbcExam jdbcExam = JdbcExam.of(exam);
        JdbcSolutionAttempt jdbcSolutionAttempt = JdbcSolutionAttempt.builder()
                .exam(jdbcExam)
                .id(solutionAttempt.getId())
                .success(solutionAttempt.isSuccess())
                .time(ZonedDateTime.now())
                .firstName(solutionAttempt.getFirstName())
                .lastName(solutionAttempt.getLastName())
                .userId(solutionAttempt.getUserId())
                .build();
        springDataSolutionAttemptRepository.save(jdbcSolutionAttempt);
    }
}



