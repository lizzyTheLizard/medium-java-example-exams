package izzy.medium.security.exams.app;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Exam {
    @NotNull
    @Getter
    @Id
    private UUID id;

    @NotEmpty
    @Getter
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Question> questions;

    @NotEmpty
    @Getter
    @Column
    private String owner;

    @NotEmpty
    @Getter
    @Column
    private String text;

    @NotEmpty
    @Getter
    @Column
    private int maxAttempts;

    @Builder
    private Exam(@Singular List<Question> questions, Principal principal, String text, int maxAttempts) {
        this.id = UUID.randomUUID();
        this.questions = questions;
        this.owner = principal.getName();
        this.text = text;
        this.maxAttempts = maxAttempts;
    }
}
