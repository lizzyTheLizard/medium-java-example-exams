package lizzy.medium.example.exams.domain.model;

import lombok.*;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @NonNull
    String id;
    @NonNull
    String firstName;
    @NonNull
    String lastName;
}
