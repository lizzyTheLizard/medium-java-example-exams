package lizzy.medium.example.exams.web.exams;

import lizzy.medium.example.exams.domain.Participation;
import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
@Builder
class ParticipationDto {
    private final String userId;
    private final String firstName;
    private final String lastName;
    private final boolean successful;
    private final int remainingAttempts;
    private final String comment;
    private final ZonedDateTime time;

    static ParticipationDto of(Participation participation) {
        return ParticipationDto.builder()
                .userId(participation.getUser().getId())
                .firstName(participation.getUser().getFirstName())
                .lastName(participation.getUser().getLastName())
                .successful(participation.isSuccessful())
                .remainingAttempts(participation.getRemainingAttempts())
                .comment(participation.getComment())
                .time(participation.getTime())
                .build();
    }
}