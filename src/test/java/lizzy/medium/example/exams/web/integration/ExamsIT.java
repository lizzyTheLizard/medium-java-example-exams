package lizzy.medium.example.exams.web.integration;

import lizzy.medium.example.exams.domain.Exam;
import lizzy.medium.example.exams.domain.ExamFactory;
import lizzy.medium.example.exams.domain.ExamRepository;
import lizzy.medium.example.exams.domain.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.UUID;

import static lizzy.medium.example.exams.web.integration.ITSecurityConfig.generateToken;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@Transactional
class ExamsIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ExamRepository examRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExamFactory examFactory;

    private Exam exam;
    private Question question;

    @BeforeEach
    void setup() {
        entityManager.createNativeQuery("TRUNCATE TABLE Exam").executeUpdate();
        exam = examFactory.create()
                .id(UUID.randomUUID())
                .maxAttempts(2)
                .closed(false)
                .ownerId("user")
                .text("text")
                .title("title")
                .build();
        examRepository.add(exam);
        entityManager.createNativeQuery("TRUNCATE TABLE Question").executeUpdate();
        question = Question.builder()
                .correctOption(1)
                .id(UUID.randomUUID())
                .text("text1")
                .options(Collections.singletonList("option1"))
                .build();
        exam.addQuestion(question);
    }

    @Test
    void getAllForUser() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/exams")
                .header(HttpHeaders.AUTHORIZATION, "bearer " + generateToken("user"));

        mockMvc.perform(request)
                .andDo(System.out::println)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(exam.getId().toString()))
                .andExpect(jsonPath("$[0].title").value(exam.getTitle()))
                .andExpect(jsonPath("$[0].text").value(exam.getText()))
                .andExpect(jsonPath("$[0].owner").value(exam.getOwnerId()))
                .andExpect(jsonPath("$[0].questions.length()").value(1))
                .andExpect(jsonPath("$[0].questions[0].text").value(question.getText()))
                .andExpect(jsonPath("$[0].questions[0].options.length()").value(question.getOptions().size()))
                .andExpect(jsonPath("$[0].questions[0].options[0]").value(question.getOptions().get(0)));
    }

    @Test
    void getAllForUserEmpty() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/exams")
                .header(HttpHeaders.AUTHORIZATION, "bearer " + generateToken("user2"));

        mockMvc.perform(request)
                .andDo(System.out::println)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void solve() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/exams/" + exam.getId() + "/solution")
                .header(HttpHeaders.AUTHORIZATION, "bearer " + generateToken("user2"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"answers\": [1], \"comment\": \"test\"}");

        mockMvc.perform(request)
                .andDo(System.out::println)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void solveWrong() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/exams/" + exam.getId() + "/solution")
                .header(HttpHeaders.AUTHORIZATION, "bearer " + generateToken("user2"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"answers\": [2], \"comment\": \"test\"}");

        mockMvc.perform(request)
                .andDo(System.out::println)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(false));
    }
}
