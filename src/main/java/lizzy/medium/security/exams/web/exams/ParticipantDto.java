package lizzy.medium.security.exams.web.exams;

import lizzy.medium.security.exams.domain.Participant;
import lizzy.medium.security.exams.domain.ParticipantStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
class ParticipantDto {
    private final String userId;
    private final String firstName;
    private final String lastName;
    private final ParticipantStatus status;

    static ParticipantDto of(Participant participant) {
        return ParticipantDto.builder()
                .userId(participant.getUserId())
                .firstName(participant.getFirstName())
                .lastName(participant.getLastName())
                .status(participant.getStatus())
                .build();
    }
}