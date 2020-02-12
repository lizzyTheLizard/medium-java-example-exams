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
    private RiddleRepository riddleRepository;

    @LocalServerPort
    private String port;

    private final Riddle riddle  = Riddle.builder()
                .principal(() -> "testUser")
                .text("This is a Riddle")
                .question(Question.builder()
                        .answer("Answer 1")
                        .correctSolution(0)
                        .text("This is a question")
                        .build())
                .question(Question.builder()
                        .answer("Answer 1")
                        .answer("Answer 2")
                        .correctSolution(1)
                        .text("This is a question 2")
                        .build()
                )
                .build();

    @Test
    void getSingle() {
        riddleRepository.save(riddle);

        String url = "http://localhost:" + port + "/riddle/" + riddle.getId().toString() + "/";
        log.info("Access " + url);
        ResponseEntity<Riddle> response = restTemplate.getForEntity( url, Riddle.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Riddle result = response.getBody();
        Assertions.assertNotNull(result);
        Assertions.assertEquals("This is a Riddle", result.getText());
        Assertions.assertEquals("This is a question", result.getQuestions().get(0).getText());
        Assertions.assertEquals("This is a question 2", result.getQuestions().get(1).getText());
        Assertions.assertEquals("Answer 1", result.getQuestions().get(0).getAnswers().get(0));
        Assertions.assertEquals("Answer 1", result.getQuestions().get(1).getAnswers().get(0));
        Assertions.assertEquals("Answer 2", result.getQuestions().get(1).getAnswers().get(1));
    }

    @Test
    void getAll() {
        riddleRepository.save(riddle);

        String url = "http://localhost:" + port + "/riddle/";
        log.info("Access " + url);
        ResponseEntity<Riddle[]> response = restTemplate.getForEntity( url, Riddle[].class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Riddle[] result = response.getBody();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.length);
    }

}