package lizzy.medium.example.exams.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.UUID;

@Value
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Participation {
    UUID id;
    User user;
    boolean successful;
    int remainingAttempts;
    String comment;
    ZonedDateTime time;
}
