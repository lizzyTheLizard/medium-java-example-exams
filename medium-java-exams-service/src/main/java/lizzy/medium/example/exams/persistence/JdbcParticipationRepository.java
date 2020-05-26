package lizzy.medium.example.exams.persistence;

import lizzy.medium.example.exams.domain.model.Exam;
import lizzy.medium.example.exams.domain.model.Participation;
import lizzy.medium.example.exams.domain.model.ParticipationRepository;
import lizzy.medium.example.exams.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JdbcParticipationRepository implements ParticipationRepository {
    private final JdbcParticipationSpringDataRepository springDataSolutionAttemptRepository;

    @Override
    public int getNumberOfFailed(Exam exam, User user) {
        JdbcExam jdbcExam = JdbcExam.of(exam);
        return springDataSolutionAttemptRepository.countByExamAndUserIdAndSuccessful(jdbcExam, user.getId(), false);
    }

    @Override
    public List<Participation> getMostRecentForeachUser(Exam exam) {
        JdbcExam jdbcExam = JdbcExam.of(exam);
        return springDataSolutionAttemptRepository.findLatestForExam(jdbcExam).stream()
                .map(JdbcParticipation::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void add(Exam exam, Participation participation) {
        JdbcParticipation jdbcParticipation = JdbcParticipation.of(participation, exam);
        springDataSolutionAttemptRepository.save(jdbcParticipation);
    }
}



