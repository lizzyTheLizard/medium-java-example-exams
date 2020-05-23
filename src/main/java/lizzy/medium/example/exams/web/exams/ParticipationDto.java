package lizzy.medium.example.exams.web.exams;

import lizzy.medium.example.exams.domain.model.Participation;
import lombok.Builder;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
@Builder
class ParticipationDto {
    String userId;
    String firstName;
    String lastName;
    boolean successful;
    int remainingAttempts;
    String comment;
    ZonedDateTime time;

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