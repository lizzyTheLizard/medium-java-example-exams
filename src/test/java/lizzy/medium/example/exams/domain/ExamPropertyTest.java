package lizzy.medium.example.exams.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ExamPropertyTest extends ExamTestBase {
    private static final User user1 = User.builder()
            .id("owner")
            .firstName("first")
            .lastName("last")
            .build();
    private static final User user2 = User.builder()
            .id("notOwner")
            .firstName("first")
            .lastName("last")
            .build();
    private static final Participation participation = Participation.builder()
            .remainingAttempts(1)
            .successful(false)
            .user(user1)
            .time(ZonedDateTime.now())
            .comment("test")
            .id(UUID.randomUUID())
            .build();

    @Test
    void isOwnedBy() {
        Exam target = examBuilder.build();

        Assertions.assertTrue(target.isOwnedBy(user1));
        Assertions.assertFalse(target.isOwnedBy(user2));
    }

    @Test
    void hasAttemptsLeft() {
        Exam target = examBuilder.maxAttempts(5).build();
        Mockito.when(participationRepository.getNumberOfFailed(target, user1)).thenReturn(4);
        Mockito.when(participationRepository.getNumberOfFailed(target, user2)).thenReturn(5);

        Assertions.assertTrue(target.hasAttemptsLeft(user1));
        Assertions.assertFalse(target.hasAttemptsLeft(user2));
    }

    @Test
    void getTriesLeft() {
        Exam target = examBuilder.maxAttempts(5).build();
        Mockito.when(participationRepository.getNumberOfFailed(target, user1)).thenReturn(2);
        Mockito.when(participationRepository.getNumberOfFailed(target, user2)).thenReturn(5);

        Assertions.assertEquals(3, target.getTriesLeft(user1));
        Assertions.assertEquals(0, target.getTriesLeft(user2));
    }

    @Test
    void getQuestions() {
        Exam target = examBuilder.build();
        List<Question> questions = Collections.singletonList(Mockito.mock(Question.class));
        Mockito.when(questionRepository.getQuestions(target)).thenReturn(questions);

        Assertions.assertEquals(questions, target.getQuestions());
    }

    @Test
    void addQuestion() {
        Exam target = examBuilder.build();
        Question question = Mockito.mock(Question.class);

        target.addQuestion(question);

        Mockito.verify(questionRepository).add(target, question);
    }

    @Test
    void getParticipants() {
        Exam target = examBuilder.build();
        List<Participation> participations = new LinkedList<>();
        participations.add(participation);
        Mockito.when(participationRepository.getMostRecentForeachUser(target)).thenReturn(participations);

        Assertions.assertEquals(participations, target.getParticipations());
    }
}
