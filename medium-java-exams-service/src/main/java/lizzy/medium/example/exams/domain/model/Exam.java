package lizzy.medium.example.exams.domain.model;

import lombok.*;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Exam {
    @NonNull
    UUID id;
    @NonNull
    String text;
    @NonNull
    String title;
    @NonNull
    String ownerId;
    int maxAttempts;
    boolean closed;

    public boolean isOwnedBy(User user) {
        return ownerId.equals(user.getId());
    }
}
