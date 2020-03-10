package lizzy.medium.security.exams.domain;

import lombok.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Exam {
    @NonNull
    private final SolutionAttemptRepository solutionAttemptRepository;
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
    private final String owner;
    private final int maxAttempts;
    private boolean closed;

    public boolean isOwnedBy(Principal principal) {
        return owner.equals(principal.getName());
    }

    public boolean hasAttemptsLeft(Principal principal) {
        return getTriesLeft(principal) > 0;
    }

    public int getTriesLeft(Principal principal) {
        return maxAttempts - getNumberOfAttempt(principal);
    }

    private int getNumberOfAttempt(Principal principal) {
        return solutionAttemptRepository
                .countForExamAndUser(this, principal.getName());
    }

    public boolean checkSolution(List<Integer> answers) {
        List<Question> questions = questionRepository.findForExam(this);
        if (questions.size() != answers.size()) {
            return false;
        }
        return IntStream.range(0, questions.size())
                .allMatch(i -> questions.get(i).isCorrectOption(answers.get(i)));
    }

    public List<Participant> getParticipants() {
        Map<String, List<SolutionAttempt>> orderByUser = new HashMap<>();
        solutionAttemptRepository.findForExam(this).forEach(sa -> addToMap(sa, orderByUser));

        return orderByUser.keySet().stream()
                .map(orderByUser::get)
                .map(list -> Participant.of(list,maxAttempts))
                .collect(Collectors.toList());
    }

    private void addToMap(SolutionAttempt solutionAttempt, Map<String, List<SolutionAttempt>> orderByUser) {
        if (!orderByUser.containsKey(solutionAttempt.getUserId())) {
            orderByUser.put(solutionAttempt.getUserId(), new LinkedList<>());
        }
        orderByUser.get(solutionAttempt.getUserId()).add(solutionAttempt);
    }

    public List<Question> getQuestions() {
        return questionRepository.findForExam(this);
    }

    public void close() {
        closed = true;
        examRepository.update(this);
    }

    public void addQuestion(Question question) {
        questionRepository.add(this, question);
    }

    public void addSolutionAttempt(String firstName, String lastName, String userId, boolean success) {
        SolutionAttempt solutionAttempt = SolutionAttempt.builder()
                .firstName(firstName)
                .lastName(lastName)
                .userId(userId)
                .id(UUID.randomUUID())
                .success(success)
                .build();
        solutionAttemptRepository.add(this, solutionAttempt);
    }
}
