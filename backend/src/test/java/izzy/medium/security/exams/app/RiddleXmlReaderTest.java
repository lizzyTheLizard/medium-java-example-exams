package izzy.medium.security.exams.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Principal;

class RiddleXmlReaderTest {
    final static String TEST = "" +
            "<?xml version=\"1.0\" ?>" +
            "<riddle>" +
            "   Explain" +
            "   <question correctSolution=\"0\">" +
            "       Question 1" +
            "       <answer>Answer 1</answer>" +
            "   </question>" +
            "   <question correctSolution=\"1\">" +
            "       Question 2" +
            "       <answer>Answer 1</answer>" +
            "       <answer>Answer 2</answer>" +
            "   </question>" +
            "</riddle>";
    private Principal principal = () -> "testUser";

    @Test
    void riddle() throws Exception{
        InputStream in = new ByteArrayInputStream(TEST.getBytes());
        RiddleXmlReader target = new RiddleXmlReader(in, principal);
        Riddle result = target.read();

        Assertions.assertEquals("Explain", result.getText());
        Assertions.assertEquals("testUser", result.getOwner());
        Assertions.assertEquals(0, result.getSolvers().size());
        Assertions.assertEquals(2, result.getQuestions().size());
    }

    @Test
    void question() throws Exception{
        InputStream in = new ByteArrayInputStream(TEST.getBytes());
        RiddleXmlReader target = new RiddleXmlReader(in, principal);
        Question result = target.read().getQuestions().get(0);

        Assertions.assertEquals("Question 1", result.getText());
        Assertions.assertEquals(0, result.getCorrectSolution());
        Assertions.assertEquals(1, result.getAnswers().size());
    }

    @Test
    void multipleQuestions() throws Exception{
        InputStream in = new ByteArrayInputStream(TEST.getBytes());
        RiddleXmlReader target = new RiddleXmlReader(in, principal);
        Question result = target.read().getQuestions().get(1);

        Assertions.assertEquals("Question 2", result.getText());
        Assertions.assertEquals(1, result.getCorrectSolution());
        Assertions.assertEquals(2, result.getAnswers().size());
    }

    @Test
    void answer() throws Exception{
        InputStream in = new ByteArrayInputStream(TEST.getBytes());
        RiddleXmlReader target = new RiddleXmlReader(in, principal);
        String result = target.read().getQuestions().get(0).getAnswers().get(0);

        Assertions.assertEquals("Answer 1", result);
    }

    @Test
    void multipleAnswer() throws Exception{
        InputStream in = new ByteArrayInputStream(TEST.getBytes());
        RiddleXmlReader target = new RiddleXmlReader(in, principal);
        String result = target.read().getQuestions().get(1).getAnswers().get(1);

        Assertions.assertEquals("Answer 2", result);
    }
}