package lizzy.medium.security.exams.rest;

import lizzy.medium.security.exams.model.Exam;
import lizzy.medium.security.exams.model.ExamRepository;
import lizzy.medium.security.exams.model.SolutionAttemptRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpStatusCodeException;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class ControllerDeleteTest {
    @Mock
    private ExamRepository examRepository;
    @Mock
    private SolutionAttemptRepository solutionAttemptRepository;
    @Mock
    private Exam exam;
    @Mock
    private Principal principal;

    @Test
    void delete() {
        UUID id = UUID.randomUUID();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(exam.getOwner()).thenReturn("testUser");
        Mockito.when(principal.getName()).thenReturn("testUser");
        Controller target = new Controller(examRepository, solutionAttemptRepository);

        target.delete(id, principal);

        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verify(exam, Mockito.times(1)).setDeleted(true);
        Mockito.verify(examRepository, Mockito.times(1)).save(exam);
        Mockito.verifyNoMoreInteractions(examRepository);
    }

    @Test
    void deleteNotAllowed() {
        UUID id = UUID.randomUUID();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(exam.getOwner()).thenReturn("someOneElse");
        Controller target = new Controller(examRepository, solutionAttemptRepository);

        Assertions.assertThrows(HttpStatusCodeException.class, () -> target.delete(id, principal));
        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(examRepository);
    }

    @Test
    void deleteNotFound() {
        UUID id = UUID.randomUUID();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.empty());
        Controller target = new Controller(examRepository, solutionAttemptRepository);

        Assertions.assertThrows(HttpStatusCodeException.class, () -> target.delete(id, principal));
        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(examRepository);
    }
}
