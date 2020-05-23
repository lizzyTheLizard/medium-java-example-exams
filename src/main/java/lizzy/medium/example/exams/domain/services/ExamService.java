package lizzy.medium.example.exams.domain.services;

import lizzy.medium.example.exams.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ParticipationRepository participationRepository;

    public Optional<Exam> findById(UUID examId) {
        return examRepository.findById(examId);
    }

    public Collection<Exam> findAllRunningOwnedBy(User user) {
        return examRepository.findAllRunningOwnedBy(user);
    }

    public Collection<Question> getQuestions(Exam exam) {
        return questionRepository.getQuestions(exam);
    }

    public Collection<Participation> getMostRecentParticipationForeachUser(Exam exam) {
        return participationRepository.getMostRecentForeachUser(exam);
    }
}
