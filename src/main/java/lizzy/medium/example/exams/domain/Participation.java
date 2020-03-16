package lizzy.medium.example.exams.domain;

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
    private final UUID id;
    private final User user;
    private final boolean successful;
    private final int remainingAttempts;
    private final String comment;
    private final ZonedDateTime time;
}
