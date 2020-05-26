package lizzy.medium.example.exams.domain.services;

import lizzy.medium.example.exams.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class ExamSolveService {
    private final ParticipationRepository participationRepository;
    private final QuestionRepository questionRepository;

    public SolveResponse solve(Exam exam, User user, List<Integer> answers, String comment) {
        int remainAttempts = getTriesLeft(exam, user);
        if (remainAttempts <= 0) {
            return SolveResponse.NO_ATTEMPTS_LEFT;
        }
        List<Question> questions = questionRepository.getQuestions(exam);
        if (questions.size() != answers.size()) {
            return SolveResponse.WRONG_INPUT;
        }
        boolean success = IntStream.range(0, questions.size())
                .allMatch(i -> questions.get(i).isCorrectOption(answers.get(i)));
        addParticipation(exam, user, comment, success, remainAttempts);
        return success ? SolveResponse.SUCCESSFUL : SolveResponse.FAILED;
    }

    private void addParticipation(Exam exam, User user, String comment, boolean success, int remainAttempts) {
        Participation participation = Participation.builder()
                .id(UUID.randomUUID())
                .user(user)
                .time(ZonedDateTime.now())
                .comment(comment)
                .successful(success)
                .remainingAttempts(success ? remainAttempts : remainAttempts - 1)
                .build();
        participationRepository.add(exam, participation);
    }

    public int getTriesLeft(Exam exam, User user) {
        int numberOfFailedAttempt = participationRepository.getNumberOfFailed(exam, user);
        return exam.getMaxAttempts() - numberOfFailedAttempt;
    }

    public enum SolveResponse {
        SUCCESSFUL, FAILED, NO_ATTEMPTS_LEFT, WRONG_INPUT
    }
}
