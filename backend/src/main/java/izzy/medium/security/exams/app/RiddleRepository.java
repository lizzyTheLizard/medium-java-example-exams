package izzy.medium.security.exams.app;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.security.Principal;
import java.util.Collection;
import java.util.UUID;

@Repository
public interface RiddleRepository extends CrudRepository<Riddle, UUID> {
    @Query("select r from Riddle r where r.owner = :#{#principal.getName()}")
    Collection<Riddle> findByUser(Principal principal);
}
