package lizzy.medium.security.exams.model;

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
public class Question {
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
    private List<String> options;

    @PositiveOrZero
    @Getter
    @Column
    private int correctOption;

    @Builder
    private Question(@Singular List<String> options, String text, int correctOption) {
        this.id = UUID.randomUUID();
        this.options = options;
        this.correctOption = correctOption;
        this.text = text;
    }
}
