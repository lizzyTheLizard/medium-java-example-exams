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
class Riddle {
    @NotNull
    @Getter
    @Id
    private UUID id;

    @NotNull
    @Getter
    @ElementCollection
    private List<String> solvers;

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

    @Builder
    private Riddle(@Singular List<Question> questions, Principal principal, String text) {
        this.id = UUID.randomUUID();
        this.questions = questions;
        this.owner = principal.getName();
        this.solvers = new LinkedList<>();
        this.text = text;
    }

    void addSolver(String name) {
        solvers.add(name);
    }
}
