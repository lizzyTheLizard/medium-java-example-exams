package izzy.medium.security.exams.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.*;


@ExtendWith(MockitoExtension.class)
class ControllerTest {
    @Mock
    private ExamRepository examRepository;
    @Mock
    private SolutionAttemptRepository solutionAttemptRepository;
    @Mock
    private Exam exam;
    @Mock
    private Question question;

    private Principal principal = () -> "testUser";

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

    @Test
    void delete() {
        UUID id = UUID.randomUUID();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(exam.getOwner()).thenReturn("testUser");
        Controller target = new Controller(examRepository, solutionAttemptRepository);

        target.delete(id, principal);

        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verify(examRepository, Mockito.times(1)).delete(exam);
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

    @Test
    void create() throws IOException {
        Controller target = new Controller(examRepository, solutionAttemptRepository);

        InputStream in = new ByteArrayInputStream(ExamXmlReaderTest.TEST.getBytes());
        MultipartFile file = new MockMultipartFile("test", in);
        Exam result = target.create(file, principal);

        Assertions.assertNotNull(result);
        Mockito.verify(examRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(examRepository);
    }

    @Test
    void trySolution() {
        Mockito.when(question.getCorrectOption()).thenReturn(1);
        List<Question> questions  = Collections.singletonList(question);
        Mockito.when(exam.getQuestions()).thenReturn(questions);
        Mockito.when(exam.getMaxAttempts()).thenReturn(2);
        UUID id = UUID.randomUUID();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(solutionAttemptRepository.countForExamAndUser(exam, principal)).thenReturn(1);

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
        Mockito.when(question.getCorrectOption()).thenReturn(1);
        List<Question> questions  = Collections.singletonList(question);
        Mockito.when(exam.getQuestions()).thenReturn(questions);
        Mockito.when(exam.getMaxAttempts()).thenReturn(2);
        UUID id = UUID.randomUUID();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(solutionAttemptRepository.countForExamAndUser(exam, principal)).thenReturn(1);

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
        UUID id = UUID.randomUUID();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.empty());

        Controller target = new Controller(examRepository, solutionAttemptRepository);
        Assertions.assertThrows(HttpStatusCodeException.class, () -> target.trySolution(id, Collections.singletonList(0),principal));

        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(examRepository);
        Mockito.verifyNoMoreInteractions(solutionAttemptRepository);
    }

    @Test
    void trySolutionToManyAttempts() {
        List<Question> questions  = Collections.singletonList(question);
        Mockito.when(exam.getMaxAttempts()).thenReturn(2);
        UUID id = UUID.randomUUID();
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(solutionAttemptRepository.countForExamAndUser(exam, principal)).thenReturn(2);

        Controller target = new Controller(examRepository, solutionAttemptRepository);
        Assertions.assertThrows(HttpStatusCodeException.class, () -> target.trySolution(id, Collections.singletonList(0),principal));

        Mockito.verify(examRepository, Mockito.times(1)).findById(id);
        Mockito.verify(solutionAttemptRepository, Mockito.times(1)).countForExamAndUser(exam, principal);
        Mockito.verifyNoMoreInteractions(examRepository);
        Mockito.verifyNoMoreInteractions(solutionAttemptRepository);
    }
}
