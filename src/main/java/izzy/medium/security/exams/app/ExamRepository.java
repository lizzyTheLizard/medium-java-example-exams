package izzy.medium.security.exams.app;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.security.Principal;
import java.util.Collection;
import java.util.UUID;

@Repository
public interface ExamRepository extends CrudRepository<Exam, UUID> {
    @Query("select r from Exam r where r.owner = :#{#principal.getName()}")
    Collection<Exam> findByUser(Principal principal);
}
