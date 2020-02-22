package lizzy.medium.security.exams.rest;

import lizzy.medium.security.exams.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.HttpStatusCodeException;

import java.security.Principal;
import java.util.*;


@ExtendWith(MockitoExtension.class)
class ControllerTrySolutionTest {
    private final UUID id = UUID.randomUUID();
    @Mock
    private ExamRepository examRepository;
    @Mock(lenient = true)
    private SolutionAttemptRepository solutionAttemptRepository;
    @Mock(lenient = true)
    private Exam exam;
    @Mock(lenient = true)
    private Question question;
    private Principal principal;

    @BeforeEach
    void setup() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("given_name", "First");
        claims.put("family_name", "Last");
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "none");
        principal = new JwtAuthenticationToken(new Jwt("something", null, null, headers, claims));


        List<Question> questions = Collections.singletonList(question);
        Mockito.when(exam.getQuestions()).thenReturn(questions);
        Mockito.when(question.getCorrectOption()).thenReturn(1);
        Mockito.when(exam.getMaxAttempts()).thenReturn(2);
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(solutionAttemptRepository.countForExamAndUser(exam, principal)).thenReturn(1);
    }

    @Test
    void trySolution() {
        Controller target = new Controller(examRepository, solutionAttemptRepository);
        boolean result = target.trySolution(id, Collections.singletonList(1), principal);

        Assertions.assertTrue(result);
        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verify(solutionAttemptRepository, Mockito.times(1)).countForExamAndUser(exam, principal);
        Mockito.verify(solutionAttemptRepository, Mockito.times(1)).save(Mockito.argThat(SolutionAttempt::isSuccess));
        Mockito.verifyNoMoreInteractions(examRepository);
        Mockito.verifyNoMoreInteractions(solutionAttemptRepository);
    }

    @Test
    void trySolutionWrong() {
        Controller target = new Controller(examRepository, solutionAttemptRepository);
        boolean result = target.trySolution(id, Collections.singletonList(0), principal);

        Assertions.assertFalse(result);
        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verify(solutionAttemptRepository, Mockito.times(1)).countForExamAndUser(exam, principal);
        Mockito.verify(solutionAttemptRepository, Mockito.times(1)).save(Mockito.argThat(s -> !s.isSuccess()));
        Mockito.verifyNoMoreInteractions(examRepository);
        Mockito.verifyNoMoreInteractions(solutionAttemptRepository);
    }

    @Test
    void trySolutionNotFound() {
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.empty());

        Controller target = new Controller(examRepository, solutionAttemptRepository);
        Assertions.assertThrows(HttpStatusCodeException.class, () -> target.trySolution(id, Collections.singletonList(0), principal));

        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(examRepository);
        Mockito.verifyNoMoreInteractions(solutionAttemptRepository);
    }

    @Test
    void trySolutionToManyAttempts() {
        Mockito.when(exam.getMaxAttempts()).thenReturn(2);
        Mockito.when(solutionAttemptRepository.countForExamAndUser(exam, principal)).thenReturn(2);

        Controller target = new Controller(examRepository, solutionAttemptRepository);
        Assertions.assertThrows(HttpStatusCodeException.class, () -> target.trySolution(id, Collections.singletonList(0), principal));

        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verify(solutionAttemptRepository, Mockito.times(1)).countForExamAndUser(exam, principal);
        Mockito.verifyNoMoreInteractions(examRepository);
        Mockito.verifyNoMoreInteractions(solutionAttemptRepository);
    }
}
