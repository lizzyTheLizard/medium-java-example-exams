package lizzy.medium.security.exams.web.integration;

import lizzy.medium.security.exams.domain.Exam;
import lizzy.medium.security.exams.domain.ExamFactory;
import lizzy.medium.security.exams.domain.ExamRepository;
import lizzy.medium.security.exams.domain.Question;
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

import java.util.Collections;
import java.util.UUID;

import static lizzy.medium.security.exams.web.integration.ITSecurityConfig.generateToken;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ExamsIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ExamRepository examRepository;

    @Autowired
    ExamFactory examFactory;

    private Exam exam;
    private Question question;

    @BeforeEach
    void setup() {
        exam = examFactory.create()
                .id(UUID.randomUUID())
                .maxAttempts(2)
                .closed(false)
                .owner("user")
                .text("text")
                .title("title")
                .build();
        examRepository.add(exam);
        question = Question.builder()
                .correctOption(1)
                .id(UUID.randomUUID())
                .text("text1")
                .options(Collections.singletonList("option1"))
                .build();
        exam.addQuestion(question);
    }

    @Test
    void test() throws Exception {
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
                .andExpect(jsonPath("$[0].owner").value(exam.getOwner()))
                .andExpect(jsonPath("$[0].questions.length()").value(1))
                .andExpect(jsonPath("$[0].questions[0].text").value(question.getText()))
                .andExpect(jsonPath("$[0].questions[0].options.length()").value(question.getOptions().size()))
                .andExpect(jsonPath("$[0].questions[0].options[0]").value(question.getOptions().get(0)));
    }
}
