package lizzy.medium.example.exams.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ExamTestBase {
    protected Exam.ExamBuilder examBuilder;

    @Mock(lenient = true)
    protected ParticipationRepository participationRepository;

    @Mock(lenient = true)
    protected QuestionRepository questionRepository;

    @Mock(lenient = true)
    protected ExamRepository examRepository;

    @BeforeEach
    void setup() {
        examBuilder = Exam.builder()
                .questionRepository(questionRepository)
                .participationRepository(participationRepository)
                .examRepository(examRepository)
                .text("text")
                .closed(false)
                .maxAttempts(1)
                .ownerId("owner")
                .title("title")
                .id(UUID.randomUUID());
    }

}
