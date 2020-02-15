package izzy.medium.security.exams.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Principal;

class ExamXmlReaderTest {
    //TODO replace with simple exam
    final static String TEST = "" +
            "<?xml version=\"1.0\" ?>" +
            "<exam maxAttempts=\"1\">" +
            "   Explain" +
            "   <question correctSolution=\"0\">" +
            "       Question 1" +
            "       <option>Answer 1</option>" +
            "   </question>" +
            "   <question correctSolution=\"1\">" +
            "       Question 2" +
            "       <option>Answer 1</option>" +
            "       <option>Answer 2</option>" +
            "   </question>" +
            "</exam>";
    private Principal principal = () -> "testUser";

    @Test
    void riddle() throws Exception{
        InputStream in = new ByteArrayInputStream(TEST.getBytes());
        ExamXmlReader target = new ExamXmlReader(in, principal);
        Exam result = target.read();

        Assertions.assertEquals("Explain", result.getText());
        Assertions.assertEquals("testUser", result.getOwner());
        Assertions.assertEquals(1, result.getMaxAttempts());
        Assertions.assertEquals(2, result.getQuestions().size());
    }

    @Test
    void question() throws Exception{
        InputStream in = new ByteArrayInputStream(TEST.getBytes());
        ExamXmlReader target = new ExamXmlReader(in, principal);
        Question result = target.read().getQuestions().get(0);

        Assertions.assertEquals("Question 1", result.getText());
        Assertions.assertEquals(0, result.getCorrectOption());
        Assertions.assertEquals(1, result.getOptions().size());
    }

    @Test
    void multipleQuestions() throws Exception{
        InputStream in = new ByteArrayInputStream(TEST.getBytes());
        ExamXmlReader target = new ExamXmlReader(in, principal);
        Question result = target.read().getQuestions().get(1);

        Assertions.assertEquals("Question 2", result.getText());
        Assertions.assertEquals(1, result.getCorrectOption());
        Assertions.assertEquals(2, result.getOptions().size());
    }

    @Test
    void answer() throws Exception{
        InputStream in = new ByteArrayInputStream(TEST.getBytes());
        ExamXmlReader target = new ExamXmlReader(in, principal);
        String result = target.read().getQuestions().get(0).getOptions().get(0);

        Assertions.assertEquals("Answer 1", result);
    }

    @Test
    void multipleAnswer() throws Exception{
        InputStream in = new ByteArrayInputStream(TEST.getBytes());
        ExamXmlReader target = new ExamXmlReader(in, principal);
        String result = target.read().getQuestions().get(1).getOptions().get(1);

        Assertions.assertEquals("Answer 2", result);
    }
}
