package lizzy.medium.security.exams.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExamAddTest extends ExamCloseTestBase {
    @Test
    void addQuestion() {
        Question question = Mockito.mock(Question.class);

        Exam target = examBuilder.build();
        target.addQuestion(question);

        Mockito.verify(questionRepository).add(target, question);
    }

    @Test
    void addSolutionAttempt() {
        Exam target = examBuilder.build();
        target.addSolutionAttempt("first", "last", "user", true);

        ArgumentCaptor<SolutionAttempt> argumentCaptor = ArgumentCaptor.forClass(SolutionAttempt.class);
        Mockito.verify(solutionAttemptRepository).add(Mockito.eq(target), argumentCaptor.capture());
        SolutionAttempt result = argumentCaptor.getValue();
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals("user", result.getUserId());
        Assertions.assertEquals("first", result.getFirstName());
        Assertions.assertEquals("last", result.getLastName());
    }
}
