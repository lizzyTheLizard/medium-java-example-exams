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
import java.util.*;


@ExtendWith(MockitoExtension.class)
class ControllerGetExamsTest {
    @Mock
    private ExamRepository examRepository;
    @Mock
    private SolutionAttemptRepository solutionAttemptRepository;
    @Mock
    private Exam exam;
    @Mock
    private Principal principal;

    @Test
    void list() {
        List<Exam> expectedExams = Collections.singletonList(exam);
        Mockito.when(examRepository.findByUser(principal)).thenReturn(expectedExams);
        Controller target = new Controller(examRepository, solutionAttemptRepository);

        Collection<Exam> result = target.list(principal);

        Assertions.assertEquals(expectedExams, result);
        Mockito.verify(examRepository, Mockito.times(1)).findByUser(principal);
        Mockito.verifyNoMoreInteractions(examRepository);
    }

    @Test
    void get() {
        UUID id = UUID.randomUUID();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Controller target = new Controller(examRepository, solutionAttemptRepository);

        Exam result = target.get(id);

        Assertions.assertEquals(exam, result);
        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(examRepository);
    }

    @Test
    void getNotFound() {
        UUID id = UUID.randomUUID();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.empty());
        Controller target = new Controller(examRepository, solutionAttemptRepository);

        Assertions.assertThrows(HttpStatusCodeException.class, () -> target.get(id));
        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(examRepository);
    }
}
