package lizzy.medium.example.exams.domain;

import lizzy.medium.example.exams.domain.model.Exam;
import lizzy.medium.example.exams.domain.model.Participation;
import lizzy.medium.example.exams.domain.model.Question;
import lizzy.medium.example.exams.domain.model.User;

import java.time.ZonedDateTime;
import java.util.UUID;

public class TestData {

    public final static User user = User.builder()
            .id("owner")
            .firstName("first")
            .lastName("last")
            .build();
    public final static Question question = Question.builder()
            .id(UUID.randomUUID())
            .correctOption(1)
            .text("Text")
            .option("Option 1")
            .build();
    public final static Participation participation = Participation.builder()
            .id(UUID.randomUUID())
            .time(ZonedDateTime.now())
            .comment(" test")
            .remainingAttempts(1)
            .user(user)
            .build();
    public static Exam exam = Exam.builder()
            .id(UUID.randomUUID())
            .text("text")
            .closed(false)
            .maxAttempts(1)
            .ownerId("owner")
            .title("title")
            .build();
}
