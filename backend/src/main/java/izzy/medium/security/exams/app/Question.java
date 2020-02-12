package izzy.medium.security.exams.app;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Question {
    @NotNull
    @Getter
    @Id
    private UUID id;

    @NotEmpty
    @Getter
    @Column
    private String text;

    @NotEmpty
    @Getter
    @ElementCollection
    private List<String> answers;

    @PositiveOrZero
    @Getter
    @Column
    private int correctSolution;

    @Builder
    private Question(@Singular List<String> answers, String text, int correctSolution) {
        this.id = UUID.randomUUID();
        this.answers = answers;
        this.correctSolution = correctSolution;
        this.text = text;
    }

}
