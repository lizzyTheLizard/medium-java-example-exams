package lizzy.medium.security.exams.helper;

import lizzy.medium.security.exams.model.Exam;
import lizzy.medium.security.exams.model.Question;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;

class ExamXmlReaderTest {
    private static final Principal principal = () -> "testUser";

    @Test
    void riddle() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(in, principal);
        Exam result = target.read();

        Assertions.assertEquals("Explain", result.getText());
        Assertions.assertEquals("testUser", result.getOwner());
        Assertions.assertEquals("Test Exam", result.getTitle());
        Assertions.assertEquals(1, result.getMaxAttempts());
        Assertions.assertEquals(2, result.getQuestions().size());
    }

    @Test
    void question() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(in, principal);
        Question result = target.read().getQuestions().get(0);

        Assertions.assertEquals("Question 1", result.getText());
        Assertions.assertEquals(0, result.getCorrectOption());
        Assertions.assertEquals(1, result.getOptions().size());
    }

    @Test
    void multipleQuestions() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(in, principal);
        Question result = target.read().getQuestions().get(1);

        Assertions.assertEquals("Question 2", result.getText());
        Assertions.assertEquals(1, result.getCorrectOption());
        Assertions.assertEquals(2, result.getOptions().size());
    }

    @Test
    void answer() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(in, principal);
        String result = target.read().getQuestions().get(0).getOptions().get(0);

        Assertions.assertEquals("Answer 1", result);
    }

    @Test
    void multipleAnswer() throws Exception {
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(in, principal);
        String result = target.read().getQuestions().get(1).getOptions().get(1);

        Assertions.assertEquals("Answer 2", result);
    }
}
