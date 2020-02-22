package lizzy.medium.security.exams.model;

import lizzy.medium.security.exams.helper.ExamXmlReader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestConfig.class)
@AutoConfigureDataJpa
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Slf4j
class ControllerIT {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ExamRepository examRepository;
    @LocalServerPort
    private String port;
    private Exam exam;

    private static void assertEquals(Exam expected, Exam actual) {
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getMaxAttempts(), actual.getMaxAttempts());
        Assertions.assertEquals(expected.getOwner(), actual.getOwner());
        Assertions.assertEquals(expected.getText(), actual.getText());
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.isDeleted(), actual.isDeleted());
        Assertions.assertEquals(expected.getQuestions().size(), actual.getQuestions().size());
        for (int i = 0; i < expected.getQuestions().size(); i++) {
            Assertions.assertEquals(expected.getQuestions().get(i).getCorrectOption(), actual.getQuestions().get(i).getCorrectOption());
            Assertions.assertEquals(expected.getQuestions().get(i).getText(), actual.getQuestions().get(i).getText());
            Assertions.assertEquals(expected.getQuestions().get(i).getId(), actual.getQuestions().get(i).getId());
            Assertions.assertEquals(expected.getQuestions().get(i).getOptions().size(), actual.getQuestions().get(i).getOptions().size());
            for (int j = 0; j < expected.getQuestions().get(i).getOptions().size(); j++) {
                Assertions.assertEquals(expected.getQuestions().get(i).getOptions().get(j), actual.getQuestions().get(i).getOptions().get(j));
            }
        }
    }

    @BeforeEach
    void setup() throws Exception {
        examRepository.findAll().forEach(examRepository::delete);
        InputStream in = new FileInputStream(new File("src/test/exams/simple.xml"));
        ExamXmlReader target = new ExamXmlReader(in, () -> "testuser");
        this.exam = examRepository.save(target.read());
    }

    @Test
    void getSingle() {
        String url = "http://localhost:" + port + "/api/exams/" + exam.getId().toString() + "/";
        ResponseEntity<Exam> response = restTemplate.getForEntity(url, Exam.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Exam result = response.getBody();
        Assertions.assertNotNull(result);
        assertEquals(exam, result);
    }

    @Test
    void getOwn() {
        String url = "http://localhost:" + port + "/api/exams/";
        ResponseEntity<Exam[]> response = restTemplate.getForEntity(url, Exam[].class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Exam[] result = response.getBody();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.length);
        assertEquals(exam, result[0]);
    }

    @Test
    void delete() {
        Assertions.assertTrue(examRepository.findById(exam.getId()).isPresent(), "Exam has not been created");
        String url = "http://localhost:" + port + "/api/exams/" + exam.getId().toString() + "/";
        restTemplate.delete(url);
        Assertions.assertFalse(examRepository.findById(exam.getId()).isPresent(), "Exam has not been deleted");
    }

    @Test
    void create() {
        //TODO Implement test
    }

    @Test
    void trySolution() {
        //TODO Implement test
    }

    @Test
    void participants() {
        //TODO Implement test
    }
}
