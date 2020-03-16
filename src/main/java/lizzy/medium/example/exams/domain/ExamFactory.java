package lizzy.medium.example.exams.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExamFactory {
    private final ParticipationRepository participationRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;

    public Exam.ExamBuilder create() {
        return Exam.builder()
                .participationRepository(participationRepository)
                .questionRepository(questionRepository)
                .examRepository(examRepository);
    }
}
