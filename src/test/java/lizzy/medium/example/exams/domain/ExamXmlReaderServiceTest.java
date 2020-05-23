package lizzy.medium.example.exams.domain;

import lizzy.medium.example.exams.domain.model.*;
import lizzy.medium.example.exams.domain.services.ExamXmlReaderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@Import(ExamXmlReaderService.class)
class ExamXmlReaderServiceTest {
    private final static User user = User.builder()
            .id("test")
            .firstName("first")
            .lastName("last")
            .build();

    @Autowired
    private ExamXmlReaderService target;
    @MockBean
    private ExamRepository examRepository;
    @MockBean
    private QuestionRepository questionRepository;

    @Test
    void exam() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));

        Exam result = target.read(user, in);

        Assertions.assertEquals("Explain", result.getText());
        Assertions.assertEquals("test", result.getOwnerId());
        Assertions.assertEquals("Test Exam", result.getTitle());
        Assertions.assertEquals(1, result.getMaxAttempts());
    }

    @Test
    void addExamToRepository() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));

        Exam result = target.read(user, in);

        Mockito.verify(examRepository, Mockito.times(1)).add(result);
    }

    @Test
    void question() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));

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

        Exam result = target.read(user, in);

        ArgumentCaptor<Question> argumentCaptor = ArgumentCaptor.forClass(Question.class);
        Mockito.verify(questionRepository, Mockito.times(2)).add(Mockito.eq(result), argumentCaptor.capture());
        Question question = argumentCaptor.getAllValues().get(0);
        Assertions.assertEquals("Answer 1", question.getOptions().get(0));
    }

    @Test
    void multipleAnswer() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));

        Exam result = target.read(user, in);

        ArgumentCaptor<Question> argumentCaptor = ArgumentCaptor.forClass(Question.class);
        Mockito.verify(questionRepository, Mockito.times(2)).add(Mockito.eq(result), argumentCaptor.capture());
        Question question = argumentCaptor.getAllValues().get(1);
        Assertions.assertEquals("Answer 2", question.getOptions().get(1));
    }
}
