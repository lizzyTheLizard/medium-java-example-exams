package lizzy.medium.security.exams.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ExamCheckSolutionTest extends ExamCloseTestBase {
    @Test
    void checkSolutionTrue() {
        Exam target = examBuilder.build();
        List<Question> questions = new LinkedList<>();
        Question question1 = Mockito.mock(Question.class);
        Mockito.when(question1.isCorrectOption(1)).thenReturn(true);
        questions.add(question1);
        Question question2 = Mockito.mock(Question.class);
        questions.add(question2);
        Mockito.when(question2.isCorrectOption(2)).thenReturn(true);
        Mockito.when(questionRepository.findForExam(target)).thenReturn(questions);

        List<Integer> answers = new LinkedList<>();
        answers.add(1);
        answers.add(2);
        Assertions.assertTrue(target.checkSolution(answers));
    }

    @Test
    void checkSolutionFalse() {
        Exam target = examBuilder.build();
        List<Question> questions = new LinkedList<>();
        Question question1 = Mockito.mock(Question.class);
        Mockito.when(question1.isCorrectOption(1)).thenReturn(true);
        questions.add(question1);
        Question question2 = Mockito.mock(Question.class);
        questions.add(question2);
        Mockito.when(question2.isCorrectOption(2)).thenReturn(false);
        Mockito.when(questionRepository.findForExam(target)).thenReturn(questions);

        List<Integer> answers = new LinkedList<>();
        answers.add(1);
        answers.add(2);
        Assertions.assertFalse(target.checkSolution(answers));
    }


    @Test
    void checkWrongNumberOfAnswers() {
        Exam target = examBuilder.build();
        List<Question> questions = new LinkedList<>();
        Question question1 = Mockito.mock(Question.class);
        questions.add(question1);
        Question question2 = Mockito.mock(Question.class);
        questions.add(question2);
        Mockito.when(questionRepository.findForExam(target)).thenReturn(questions);

        List<Integer> answers = new LinkedList<>();
        answers.add(1);
        Assertions.assertFalse(target.checkSolution(answers));
    }
}
