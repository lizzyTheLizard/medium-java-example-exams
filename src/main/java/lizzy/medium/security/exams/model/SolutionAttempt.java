package lizzy.medium.security.exams.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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

    @NotNull
    @Getter
    @Embedded
    private Participant participant;

    @NotNull
    @ManyToOne
    private Exam exam;

    @Getter
    private boolean success;

    @NotNull
    @Getter
    private ZonedDateTime time;

    @Builder
    private SolutionAttempt(Participant participant, Exam exam, boolean success) {
        this.id = UUID.randomUUID();
        this.participant = participant;
        this.exam = exam;
        this.success = success;
        this.time = ZonedDateTime.now();
    }

}
