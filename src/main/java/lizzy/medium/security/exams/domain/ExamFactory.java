package lizzy.medium.security.exams.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExamFactory {
    private final SolutionAttemptRepository solutionAttemptRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;

    public Exam.ExamBuilder create() {
        return Exam.builder()
                .solutionAttemptRepository(solutionAttemptRepository)
                .questionRepository(questionRepository)
                .examRepository(examRepository);
    }
}
