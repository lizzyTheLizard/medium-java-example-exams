package lizzy.medium.security.exams.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
interface JdbcExamSpringDataRepository extends CrudRepository<JdbcExam, UUID> {
    Collection<JdbcExam> findByOwner(String owner);

    Optional<JdbcExam> findById(UUID examId);
}
