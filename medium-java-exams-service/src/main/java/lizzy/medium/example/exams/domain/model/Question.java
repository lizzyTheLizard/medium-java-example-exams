package lizzy.medium.example.exams.domain.model;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Question {
    UUID id;
    int correctOption;
    String text;
    @Singular
    List<String> options;

    public boolean isCorrectOption(int option) {
        return correctOption == option;
    }
}
