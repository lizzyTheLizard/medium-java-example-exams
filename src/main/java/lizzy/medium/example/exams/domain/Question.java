package lizzy.medium.example.exams.domain;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Question {
    public final int correctOption;
    private final UUID id;
    private final String text;
    @Singular
    private final List<String> options;

    public boolean isCorrectOption(int option) {
        return correctOption == option;
    }
}
