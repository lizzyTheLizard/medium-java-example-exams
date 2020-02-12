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
    private RiddleRepository repository;
    @Mock
    private Riddle riddle;
    @Mock
    private Question question;

    private Principal principal = () -> "testUser";

    @Test
    void list() {
        List<Riddle> expectedRiddles = Collections.singletonList(riddle);
        Mockito.when(repository.findByUser(principal)).thenReturn(expectedRiddles);
        Controller target = new Controller(repository);

        Collection<Riddle> result = target.list(principal);

        Assertions.assertEquals(expectedRiddles, result);
        Mockito.verify(repository, Mockito.times(1)).findByUser(principal);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void get() {
        UUID id = UUID.randomUUID();
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(riddle));
        Controller target = new Controller(repository);

        Riddle result = target.get(id);

        Assertions.assertEquals(riddle, result);
        Mockito.verify(repository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void getNotFound() {
        UUID id = UUID.randomUUID();
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Controller target = new Controller(repository);

        Assertions.assertThrows(HttpStatusCodeException.class, () -> target.get(id));
        Mockito.verify(repository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void delete() {
        UUID id = UUID.randomUUID();
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(riddle));
        Mockito.when(riddle.getOwner()).thenReturn("testUser");
        Controller target = new Controller(repository);

        target.delete(id, principal);

        Mockito.verify(repository, Mockito.times(1)).findById(id);
        Mockito.verify(repository, Mockito.times(1)).delete(riddle);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void deleteNotAllowed() {
        UUID id = UUID.randomUUID();
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(riddle));
        Mockito.when(riddle.getOwner()).thenReturn("someOneElse");
        Controller target = new Controller(repository);

        Assertions.assertThrows(HttpStatusCodeException.class, () -> target.delete(id, principal));
        Mockito.verify(repository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void deleteNotFound() {
        UUID id = UUID.randomUUID();
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Controller target = new Controller(repository);

        Assertions.assertThrows(HttpStatusCodeException.class, () -> target.delete(id, principal));
        Mockito.verify(repository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void create() throws IOException {
        Controller target = new Controller(repository);

        InputStream in = new ByteArrayInputStream(RiddleXmlReaderTest.TEST.getBytes());
        MultipartFile file = new MockMultipartFile("test", in);
        Riddle result = target.create(file, principal);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void trySolution() {
        Mockito.when(question.getCorrectSolution()).thenReturn(1);
        List<Question> questions  = Collections.singletonList(question);
        Mockito.when(riddle.getQuestions()).thenReturn(questions);
        UUID id = UUID.randomUUID();
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(riddle));

        Controller target = new Controller(repository);
        boolean result = target.trySolution(id, Collections.singletonList(1), principal);

        Assertions.assertTrue(result);
        Mockito.verify(repository, Mockito.times(1)).findById(id);
        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(riddle, Mockito.times(1)).addSolver("testUser");
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void trySolutionWrong() {
        Mockito.when(question.getCorrectSolution()).thenReturn(1);
        List<Question> questions  = Collections.singletonList(question);
        Mockito.when(riddle.getQuestions()).thenReturn(questions);
        UUID id = UUID.randomUUID();
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(riddle));

        Controller target = new Controller(repository);
        boolean result = target.trySolution(id, Collections.singletonList(0), principal);

        Assertions.assertFalse(result);
        Mockito.verify(repository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void trySolutionNotFound() {
        UUID id = UUID.randomUUID();
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Controller target = new Controller(repository);
        Assertions.assertThrows(HttpStatusCodeException.class, () -> target.trySolution(id, Collections.singletonList(0),principal));

        Mockito.verify(repository, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(repository);
    }
}