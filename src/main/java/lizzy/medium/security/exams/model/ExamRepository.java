package lizzy.medium.security.exams.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRepository extends CrudRepository<Exam, UUID> {
    @Query("select r from Exam r where r.owner = :#{#principal.getName()}")
    Collection<Exam> findByUser(Principal principal);

    @Query("select r from Exam r where deleted = false")
    Optional<Exam> findById(UUID examId);
}
