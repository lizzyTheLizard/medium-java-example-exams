package izzy.medium.security.exams.app;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SolutionAttempt {
    @NotNull
    @Getter
    @Id
    private UUID id;

    @NotEmpty
    @Getter
    private String user;

    @NotNull
    @ManyToOne
    private Exam exam;

    @Getter
    private boolean success;

    @NotNull
    @Getter
    private ZonedDateTime time;

    @Builder
    private SolutionAttempt(String user, Exam exam, boolean success) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.exam = exam;
        this.success = success;
        this.time = ZonedDateTime.now();
    }

}
