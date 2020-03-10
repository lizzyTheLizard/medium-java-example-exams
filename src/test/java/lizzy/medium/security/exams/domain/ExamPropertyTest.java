package lizzy.medium.security.exams.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ExamPropertyTest extends ExamCloseTestBase {
    @Test
    void isOwnedBy() {
        Exam target = examBuilder.build();

        Assertions.assertTrue(target.isOwnedBy(() -> "owner"));
        Assertions.assertFalse(target.isOwnedBy(() -> "notOwner"));
    }

    @Test
    void hasAttemptsLeft() {
        Exam target = examBuilder.maxAttempts(5).build();
        Mockito.when(solutionAttemptRepository.countForExamAndUser(target, "owner")).thenReturn(4);

        Assertions.assertTrue(target.hasAttemptsLeft(() -> "owner"));
    }

    @Test
    void hasNoAttemptsLeft() {
        Exam target = examBuilder.maxAttempts(5).build();
        Mockito.when(solutionAttemptRepository.countForExamAndUser(target, "owner")).thenReturn(5);

        Assertions.assertFalse(target.hasAttemptsLeft(() -> "owner"));
    }

    @Test
    void getTriesLeft() {
        Exam target = examBuilder.maxAttempts(5).build();
        Mockito.when(solutionAttemptRepository.countForExamAndUser(target, "owner")).thenReturn(2);

        Assertions.assertEquals(3, target.getTriesLeft(() -> "owner"));
    }

    @Test
    void getQuestions() {
        Exam target = examBuilder.build();
        List<Question> questions = Collections.singletonList(Mockito.mock(Question.class));
        Mockito.when(questionRepository.findForExam(target)).thenReturn(questions);

        Assertions.assertEquals(questions, target.getQuestions());
    }
}
