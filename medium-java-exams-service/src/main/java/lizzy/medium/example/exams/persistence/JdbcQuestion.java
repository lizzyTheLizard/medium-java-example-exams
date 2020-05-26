package lizzy.medium.example.exams.persistence;

import lizzy.medium.example.exams.domain.model.Exam;
import lizzy.medium.example.exams.domain.model.Question;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.UUID;

@Entity(name = "Question")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class JdbcQuestion {
    @NotNull
    @Getter
    @Id
    private UUID id;

    @NotEmpty
    @Getter
    @Column
    private String text;

    @NotNull
    @Getter
    @ManyToOne
    private JdbcExam exam;

    @NotEmpty
    @Getter
    @ElementCollection
    @Singular
    private List<String> options;

    @PositiveOrZero
    @Getter
    @Column
    private int correctOption;

    static JdbcQuestion of(Question question, Exam exam) {
        return JdbcQuestion.builder()
                .id(question.getId())
                .text(question.getText())
                .exam(JdbcExam.of(exam))
                .options(question.getOptions())
                .correctOption(question.getCorrectOption())
                .build();
    }

    public Question toEntity() {
        return Question.builder()
                .id(id)
                .options(options)
                .text(text)
                .correctOption(correctOption)
                .build();
    }
}
