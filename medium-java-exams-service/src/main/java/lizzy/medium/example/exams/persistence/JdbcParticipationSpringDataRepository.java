package lizzy.medium.example.exams.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
interface JdbcParticipationSpringDataRepository extends CrudRepository<JdbcParticipation, UUID> {
    int countByExamAndUserIdAndSuccessful(JdbcExam jdbcExam, String userId, boolean successful);

    @Query(value = "SELECT * FROM LATEST_PARTICIPATION_VIEW WHERE EXAM_ID = ?#{[0].id}", nativeQuery = true)
    List<JdbcParticipation> findLatestForExam(JdbcExam exam);
}
