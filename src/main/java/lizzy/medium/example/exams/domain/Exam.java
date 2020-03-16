package lizzy.medium.example.exams.domain;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Exam {
    @NonNull
    private final ParticipationRepository participationRepository;
    @NonNull
    private final QuestionRepository questionRepository;
    @NonNull
    private final ExamRepository examRepository;
    @NonNull
    private final UUID id;
    @NonNull
    private final String text;
    @NonNull
    private final String title;
    @NonNull
    private final String ownerId;
    private final int maxAttempts;
    private boolean closed;

    public boolean isOwnedBy(User user) {
        return ownerId.equals(user.getId());
    }

    public boolean hasAttemptsLeft(User user) {
        return getTriesLeft(user) > 0;
    }

    public int getTriesLeft(User user) {
        int numberOfFailedAttempt = participationRepository.getNumberOfFailed(this, user);
        return maxAttempts - numberOfFailedAttempt;
    }

    public List<Participation> getParticipations() {
        return participationRepository.getMostRecentForeachUser(this);
    }

    public List<Question> getQuestions() {
        return questionRepository.getQuestions(this);
    }

    public void addQuestion(Question question) {
        questionRepository.add(this, question);
    }

    public SolveResponse solve(User user, List<Integer> answers, String comment) {
        int remainAttempts = getTriesLeft(user);
        if (remainAttempts <= 0) {
            return SolveResponse.NO_ATTEMPTS_LEFT;
        }
        List<Question> questions = questionRepository.getQuestions(this);
        if (questions.size() != answers.size()) {
            return SolveResponse.WRONG_INPUT;
        }
        boolean success = IntStream.range(0, questions.size())
                .allMatch(i -> questions.get(i).isCorrectOption(answers.get(i)));
        addParticipation(user, comment, success, remainAttempts);
        return success ? SolveResponse.SUCCESSFUL : SolveResponse.FAILED;
    }

    private void addParticipation(User user, String comment, boolean success, int remainAttempts) {
        Participation participation = Participation.builder()
                .id(UUID.randomUUID())
                .user(user)
                .time(ZonedDateTime.now())
                .comment(comment)
                .successful(success)
                .remainingAttempts(success ? remainAttempts : remainAttempts - 1)
                .build();
        participationRepository.add(this, participation);
    }

    public void close() {
        closed = true;
        examRepository.update(this);
    }
}
