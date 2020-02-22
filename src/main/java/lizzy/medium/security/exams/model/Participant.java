package lizzy.medium.security.exams.model;

import lombok.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Participant {
    @NotEmpty
    @Getter
    private String userId;

    @NotEmpty
    @Getter
    private String firstName;

    @NotEmpty
    @Getter
    private String lastName;
}
