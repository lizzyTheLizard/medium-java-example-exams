package lizzy.medium.example.exams.domain;

import lombok.*;

@Value
@Builder(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @NonNull
    private final String id;
    @NonNull
    private final String firstName;
    @NonNull
    private final String lastName;
}
