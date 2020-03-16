package lizzy.medium.example.exams.web.exams;

import lizzy.medium.example.exams.domain.*;
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

@ExtendWith(MockitoExtension.class)
class JdbcExamXmlReaderTest {
    private final static User user = User.builder()
            .id("test")
            .firstName("first")
            .lastName("last")
            .build();

    @Mock
    private ExamFactory examFactory;
    @Mock
    private ParticipationRepository participationRepository;
    @Mock
    private ExamRepository examRepository;
    @Mock
    private QuestionRepository questionRepository;

    @BeforeEach
    void setup() {
        Exam.ExamBuilder builder = Exam.builder()
                .examRepository(examRepository)
                .participationRepository(participationRepository)
                .questionRepository(questionRepository);
        Mockito.when(examFactory.create()).thenReturn(builder);
    }

    @Test
    void exam() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(examFactory, examRepository);

        Exam result = target.read(user, in);

        Assertions.assertEquals("Explain", result.getText());
        Assertions.assertEquals("test", result.getOwnerId());
        Assertions.assertEquals("Test Exam", result.getTitle());
        Assertions.assertEquals(1, result.getMaxAttempts());
    }

    @Test
    void addExamToRepository() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(examFactory, examRepository);

        Exam result = target.read(user, in);

        Mockito.verify(examRepository, Mockito.times(1)).add(result);
    }

    @Test
    void question() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(examFactory, examRepository);

        Exam result = target.read(user, in);

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

        Exam result = target.read(user, in);

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

        Exam result = target.read(user, in);

        ArgumentCaptor<Question> argumentCaptor = ArgumentCaptor.forClass(Question.class);
        Mockito.verify(questionRepository, Mockito.times(2)).add(Mockito.eq(result), argumentCaptor.capture());
        Question question = argumentCaptor.getAllValues().get(0);
        Assertions.assertEquals("Answer 1", question.getOptions().get(0));
    }

    @Test
    void multipleAnswer() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(examFactory, examRepository);

        Exam result = target.read(user, in);

        ArgumentCaptor<Question> argumentCaptor = ArgumentCaptor.forClass(Question.class);
        Mockito.verify(questionRepository, Mockito.times(2)).add(Mockito.eq(result), argumentCaptor.capture());
        Question question = argumentCaptor.getAllValues().get(1);
        Assertions.assertEquals("Answer 2", question.getOptions().get(1));
    }
}
