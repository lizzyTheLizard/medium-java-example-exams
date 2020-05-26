package lizzy.medium.example.exams.persistence;

import lizzy.medium.example.exams.domain.model.Exam;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity(name = "Exam")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class JdbcExam {
    @NotNull
    @Getter
    @Id
    private UUID id;

    @NotEmpty
    @Getter
    @Column
    private String owner;

    @NotEmpty
    @Getter
    @Column
    private String title;

    @NotEmpty
    @Getter
    @Column
    private String text;

    @Getter
    @Column
    private int maxAttempts;

    @Getter
    @Setter
    @Column
    private boolean closed;


    static JdbcExam of(Exam exam) {
        JdbcExam.JdbcExamBuilder jdbcBuilder = JdbcExam.builder()
                .closed(exam.isClosed())
                .id(exam.getId())
                .maxAttempts(exam.getMaxAttempts())
                .owner(exam.getOwnerId())
                .text(exam.getText())
                .title(exam.getTitle());
        return jdbcBuilder.build();
    }


    Exam toAggregate() {
        return Exam.builder()
                .text(text)
                .ownerId(owner)
                .maxAttempts(maxAttempts)
                .title(title)
                .id(id)
                .closed(closed)
                .build();
    }
}
