package lizzy.medium.security.exams.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(access = AccessLevel.PROTECTED)
public class Participant {
    private final String userId;
    private final String firstName;
    private final String lastName;
    private final ParticipantStatus status;


    static Participant of(List<SolutionAttempt> attempts, int maxAttempts) {
        Participant.ParticipantBuilder resultBuilder = Participant.builder()
                .userId(attempts.get(0).getUserId())
                .lastName(attempts.get(0).getLastName())
                .firstName(attempts.get(0).getFirstName());
        if (attempts.stream().anyMatch(SolutionAttempt::isSuccess)) {
            return resultBuilder.status(ParticipantStatus.SUCCESS).build();
        }
        if (attempts.size() >= maxAttempts) {
            return resultBuilder.status(ParticipantStatus.FAILED).build();
        }
        return resultBuilder.status(ParticipantStatus.OPEN).build();
    }

}
