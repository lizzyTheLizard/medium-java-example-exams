package lizzy.medium.example.exams.domain;

import lizzy.medium.example.exams.domain.model.*;
import lizzy.medium.example.exams.domain.services.ExamSolveService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@Import(ExamSolveService.class)
public class ExamSolveServiceTest {
    @Autowired
    private ExamSolveService target;

    @MockBean
    private ParticipationRepository participationRepository;

    @MockBean
    private QuestionRepository questionRepository;

    @BeforeEach
    void setup() {
        Question question1 = Question.builder()
                .correctOption(1)
                .id(UUID.randomUUID())
                .text("text")
                .build();
        Question question2 = Question.builder()
                .correctOption(2)
                .id(UUID.randomUUID())
                .text("text")
                .build();

        List<Question> questions = List.of(question1, question2);
        Mockito.when(questionRepository.getQuestions(Mockito.any())).thenReturn(questions);
        Mockito.when(participationRepository.getNumberOfFailed(TestData.exam, TestData.user)).thenReturn(0);
    }

    @Test
    void solveTrue() {
        List<Integer> answers = List.of(1, 2);

        ExamSolveService.SolveResponse result = target.solve(TestData.exam, TestData.user, answers, "test");

        Assertions.assertEquals(ExamSolveService.SolveResponse.SUCCESSFUL, result);
    }

    @Test
    void solveTrueAddedParticipation() {
        List<Integer> answers = List.of(1, 2);

        target.solve(TestData.exam, TestData.user, answers, "test");

        ArgumentCaptor<Participation> argumentCaptor = ArgumentCaptor.forClass(Participation.class);
        Mockito.verify(participationRepository).add(Mockito.eq(TestData.exam), argumentCaptor.capture());
        Participation participation = argumentCaptor.getValue();
        Assertions.assertNotNull(participation.getId());
        Assertions.assertEquals(TestData.user, participation.getUser());
        Assertions.assertEquals("test", participation.getComment());
        Assertions.assertTrue(participation.isSuccessful());
    }

    @Test
    void solveFalse() {
        List<Integer> answers = List.of(1, 1);

        ExamSolveService.SolveResponse result = target.solve(TestData.exam, TestData.user, answers, "test");

        Assertions.assertEquals(ExamSolveService.SolveResponse.FAILED, result);
    }

    @Test
    void solveFalseAddedParticipation() {
        List<Integer> answers = List.of(1, 1);

        target.solve(TestData.exam, TestData.user, answers, "test");

        ArgumentCaptor<Participation> argumentCaptor = ArgumentCaptor.forClass(Participation.class);
        Mockito.verify(participationRepository).add(Mockito.eq(TestData.exam), argumentCaptor.capture());
        Participation participation = argumentCaptor.getValue();
        Assertions.assertNotNull(participation.getId());
        Assertions.assertEquals(TestData.user, participation.getUser());
        Assertions.assertEquals("test", participation.getComment());
        Assertions.assertFalse(participation.isSuccessful());
    }

    @Test
    void solveWrongNumberOfAnswers() {
        List<Integer> answers = List.of(1);

        ExamSolveService.SolveResponse result = target.solve(TestData.exam, TestData.user, answers, "test");

        Assertions.assertEquals(ExamSolveService.SolveResponse.WRONG_INPUT, result);
        Mockito.verify(participationRepository, Mockito.never()).add(Mockito.any(), Mockito.any());
    }

    @Test
    void solveNoAttemptsLeft() {
        Exam exam = TestData.exam.toBuilder().maxAttempts(2).build();
        Mockito.when(participationRepository.getNumberOfFailed(exam, TestData.user)).thenReturn(2);

        ExamSolveService.SolveResponse result = target.solve(exam, TestData.user, Collections.emptyList(), "test");

        Assertions.assertEquals(ExamSolveService.SolveResponse.NO_ATTEMPTS_LEFT, result);
        Mockito.verify(participationRepository, Mockito.never()).add(Mockito.any(), Mockito.any());
    }

    @Test
    void getTriesLeftTrue() {
        Exam exam = TestData.exam.toBuilder().maxAttempts(2).build();
        Mockito.when(participationRepository.getNumberOfFailed(exam, TestData.user)).thenReturn(1);

        int result = target.getTriesLeft(exam, TestData.user);

        Assertions.assertEquals(1, result);
    }

    @Test
    void getTriesLeftFalse() {
        Exam exam = TestData.exam.toBuilder().maxAttempts(2).build();
        Mockito.when(participationRepository.getNumberOfFailed(exam, TestData.user)).thenReturn(2);

        int result = target.getTriesLeft(exam, TestData.user);

        Assertions.assertEquals(0, result);
    }
}
