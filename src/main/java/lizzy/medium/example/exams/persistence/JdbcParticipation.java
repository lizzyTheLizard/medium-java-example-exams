package lizzy.medium.example.exams.persistence;

import lizzy.medium.example.exams.domain.Exam;
import lizzy.medium.example.exams.domain.Participation;
import lizzy.medium.example.exams.domain.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "Participation")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class JdbcParticipation {
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
    private boolean successful;

    @Getter
    private String comment;

    @NotNull
    @Getter
    private ZonedDateTime time;

    @Getter
    @PositiveOrZero
    private int remainingAttempts;

    static JdbcParticipation of(Participation participation, Exam exam) {
        return JdbcParticipation.builder()
                .id(participation.getId())
                .exam(JdbcExam.of(exam))
                .successful(participation.isSuccessful())
                .time(participation.getTime())
                .userId(participation.getUser().getId())
                .firstName(participation.getUser().getFirstName())
                .lastName(participation.getUser().getLastName())
                .remainingAttempts(participation.getRemainingAttempts())
                .comment(participation.getComment())
                .build();
    }

    Participation toEntity() {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .id(userId)
                .build();
        return Participation.builder()
                .id(id)
                .user(user)
                .successful(successful)
                .comment(comment)
                .remainingAttempts(remainingAttempts)
                .time(time)
                .build();
    }
}
