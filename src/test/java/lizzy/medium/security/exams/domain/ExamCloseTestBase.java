package lizzy.medium.security.exams.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ExamCloseTestBase {
    @Mock
    protected SolutionAttemptRepository solutionAttemptRepository;

    @Mock
    protected QuestionRepository questionRepository;

    @Mock
    protected ExamRepository examRepository;

    protected Exam.ExamBuilder examBuilder;

    @BeforeEach
    void setup() {
        examBuilder = Exam.builder()
                .questionRepository(questionRepository)
                .solutionAttemptRepository(solutionAttemptRepository)
                .examRepository(examRepository)
                .text("text")
                .closed(false)
                .maxAttempts(1)
                .owner("owner")
                .title("title")
                .id(UUID.randomUUID());
    }

}
