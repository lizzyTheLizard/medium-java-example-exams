package lizzy.medium.example.exams.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
interface JdbcQuestionSpringDataRepository extends CrudRepository<JdbcQuestion, UUID> {
    List<JdbcQuestion> findByExam(JdbcExam exam);
}
