package lizzy.medium.security.exams.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SolutionAttempt {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final String userId;
    private final String comment;
    private boolean success;
}
