package lizzy.medium.security.exams.web.exams;

import lizzy.medium.security.exams.domain.*;
import lizzy.medium.security.exams.web.exams.ExamXmlReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;

@ExtendWith(MockitoExtension.class)
class JdbcExamXmlReaderTest {
    private static final Principal principal = () -> "testUser";

    @Mock
    private ExamFactory examFactory;
    @Mock
    private SolutionAttemptRepository solutionAttemptRepository;
    @Mock
    private ExamRepository examRepository;
    @Mock
    private QuestionRepository questionRepository;

    @BeforeEach
    void setup(){
        Exam.ExamBuilder builder = Exam.builder()
                .examRepository(examRepository)
                .solutionAttemptRepository(solutionAttemptRepository)
                .questionRepository(questionRepository);
        Mockito.when(examFactory.create()).thenReturn(builder);
    }

    @Test
    void exam() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(examFactory, examRepository);

        Exam result = target.read(principal, in);

        Assertions.assertEquals("Explain", result.getText());
        Assertions.assertEquals("testUser", result.getOwner());
        Assertions.assertEquals("Test Exam", result.getTitle());
        Assertions.assertEquals(1, result.getMaxAttempts());
    }

    @Test
    void addExamToRepository() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(examFactory, examRepository);

        Exam result = target.read(principal, in);

        Mockito.verify(examRepository, Mockito.times(1)).add(result);
    }

    @Test
    void question() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(examFactory, examRepository);

        Exam result = target.read(principal, in);

        ArgumentCaptor<Question> argumentCaptor = ArgumentCaptor.forClass(Question.class);
        Mockito.verify(questionRepository, Mockito.times(2)).add(Mockito.eq(result), argumentCaptor.capture());
        Question question = argumentCaptor.getAllValues().get(0);
        Assertions.assertEquals("Question 1", question.getText());
        Assertions.assertEquals(0, question.getCorrectOption());
        Assertions.assertEquals(1, question.getOptions().size());
    }

    @Test
    void multipleQuestions() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(examFactory, examRepository);

        Exam result = target.read(principal, in);

        ArgumentCaptor<Question> argumentCaptor = ArgumentCaptor.forClass(Question.class);
        Mockito.verify(questionRepository, Mockito.times(2)).add(Mockito.eq(result), argumentCaptor.capture());
        Question question = argumentCaptor.getAllValues().get(1);
        Assertions.assertEquals("Question 2", question.getText());
        Assertions.assertEquals(1, question.getCorrectOption());
        Assertions.assertEquals(2, question.getOptions().size());
    }

    @Test
    void answer() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(examFactory, examRepository);

        Exam result = target.read(principal, in);

        ArgumentCaptor<Question> argumentCaptor = ArgumentCaptor.forClass(Question.class);
        Mockito.verify(questionRepository, Mockito.times(2)).add(Mockito.eq(result), argumentCaptor.capture());
        Question question = argumentCaptor.getAllValues().get(0);
        Assertions.assertEquals("Answer 1", question.getOptions().get(0));
    }

    @Test
    void multipleAnswer() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(examFactory, examRepository);

        Exam result = target.read(principal, in);

        ArgumentCaptor<Question> argumentCaptor = ArgumentCaptor.forClass(Question.class);
        Mockito.verify(questionRepository, Mockito.times(2)).add(Mockito.eq(result), argumentCaptor.capture());
        Question question = argumentCaptor.getAllValues().get(1);
        Assertions.assertEquals("Answer 2", question.getOptions().get(1));
    }
}
