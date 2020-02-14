package izzy.medium.security.exams.app;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureDataJpa
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Slf4j
class ControllerIT {

    //TODO does not work and is not connected!


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ExamRepository examRepository;

    @LocalServerPort
    private String port;

    private final Exam exam = Exam.builder()
                .principal(() -> "testUser")
                .text("This is a Riddle")
                .question(Question.builder()
                        .option("Answer 1")
                        .correctOption(0)
                        .text("This is a question")
                        .build())
                .question(Question.builder()
                        .option("Answer 1")
                        .option("Answer 2")
                        .correctOption(1)
                        .text("This is a question 2")
                        .build()
                )
                .build();

    @Test
    void getSingle() {
        examRepository.save(exam);

        String url = "http://localhost:" + port + "/riddle/" + exam.getId().toString() + "/";
        log.info("Access " + url);
        ResponseEntity<Exam> response = restTemplate.getForEntity( url, Exam.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Exam result = response.getBody();
        Assertions.assertNotNull(result);
        Assertions.assertEquals("This is a Riddle", result.getText());
        Assertions.assertEquals("This is a question", result.getQuestions().get(0).getText());
        Assertions.assertEquals("This is a question 2", result.getQuestions().get(1).getText());
        Assertions.assertEquals("Answer 1", result.getQuestions().get(0).getOptions().get(0));
        Assertions.assertEquals("Answer 1", result.getQuestions().get(1).getOptions().get(0));
        Assertions.assertEquals("Answer 2", result.getQuestions().get(1).getOptions().get(1));
    }

    @Test
    void getAll() {
        examRepository.save(exam);

        String url = "http://localhost:" + port + "/riddle/";
        log.info("Access " + url);
        ResponseEntity<Exam[]> response = restTemplate.getForEntity( url, Exam[].class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Exam[] result = response.getBody();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.length);
    }

}
