package lizzy.medium.example.exams.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ExamSolveTest extends ExamTestBase {
    private final static User user = User.builder()
            .lastName("last")
            .firstName("first")
            .id("id")
            .build();

    @Mock(lenient = true)
    private Question question1;

    @Mock(lenient = true)
    private Question question2;

    @BeforeEach
    void setup() {
        super.setup();
        Mockito.when(question1.isCorrectOption(1)).thenReturn(true);
        Mockito.when(question2.isCorrectOption(2)).thenReturn(true);
        List<Question> questions = new LinkedList<>();
        questions.add(question1);
        questions.add(question2);
        Mockito.when(questionRepository.getQuestions(Mockito.any())).thenReturn(questions);
    }

    @Test
    void solveTrue() {
        Exam target = examBuilder.build();
        List<Integer> answers = new LinkedList<>();
        answers.add(1);
        answers.add(2);

        SolveResponse result = target.solve(user, answers, "test");

        Assertions.assertEquals(SolveResponse.SUCCESSFUL, result);
        ArgumentCaptor<Participation> argumentCaptor = ArgumentCaptor.forClass(Participation.class);
        Mockito.verify(participationRepository).add(Mockito.eq(target), argumentCaptor.capture());
        Participation participation = argumentCaptor.getValue();
        Assertions.assertNotNull(participation.getId());
        Assertions.assertEquals(user, participation.getUser());
        Assertions.assertEquals("test", participation.getComment());
        Assertions.assertTrue(participation.isSuccessful());
    }

    @Test
    void solveFalse() {
        Exam target = examBuilder.build();
        List<Integer> answers = new LinkedList<>();
        answers.add(1);
        answers.add(1);

        SolveResponse result = target.solve(user, answers, "test");

        Assertions.assertEquals(SolveResponse.FAILED, result);
        ArgumentCaptor<Participation> argumentCaptor = ArgumentCaptor.forClass(Participation.class);
        Mockito.verify(participationRepository).add(Mockito.eq(target), argumentCaptor.capture());
        Participation participation = argumentCaptor.getValue();
        Assertions.assertNotNull(participation.getId());
        Assertions.assertEquals(user, participation.getUser());
        Assertions.assertEquals("test", participation.getComment());
        Assertions.assertFalse(participation.isSuccessful());
    }

    @Test
    void wrongNumberOfAnswers() {
        Exam target = examBuilder.build();
        List<Integer> answers = new LinkedList<>();
        answers.add(1);

        SolveResponse result = target.solve(user, answers, "test");

        Assertions.assertEquals(SolveResponse.WRONG_INPUT, result);
        Mockito.verify(participationRepository, Mockito.never()).add(Mockito.any(), Mockito.any());
    }

    @Test
    void noAttemptsLeft() {
        Exam target = examBuilder.maxAttempts(2).build();
        Mockito.when(participationRepository.getNumberOfFailed(target, user)).thenReturn(2);

        SolveResponse result = target.solve(user, Collections.emptyList(), "test");

        Assertions.assertEquals(SolveResponse.NO_ATTEMPTS_LEFT, result);
        Mockito.verify(participationRepository, Mockito.never()).add(Mockito.any(), Mockito.any());
    }
}
