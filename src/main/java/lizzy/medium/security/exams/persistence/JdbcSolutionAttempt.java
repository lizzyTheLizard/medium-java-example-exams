package lizzy.medium.security.exams.persistence;

import lizzy.medium.security.exams.domain.SolutionAttempt;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity(name="SolutionAttempt")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class JdbcSolutionAttempt {
    @NotNull
    @Getter
    @Id
    private UUID id;

    @NotEmpty
    @Getter
    private String userId;

    @NotEmpty
    @Getter
    private String firstName;

    @NotEmpty
    @Getter
    private String lastName;

    @NotNull
    @ManyToOne
    private JdbcExam exam;

    @Getter
    private boolean success;

    @NotNull
    @Getter
    private ZonedDateTime time;

    SolutionAttempt toEntity() {
        return SolutionAttempt.builder()
                .firstName(firstName)
                .lastName(lastName)
                .id(id)
                .success(success)
                .userId(userId)
                .build();
    }

}
