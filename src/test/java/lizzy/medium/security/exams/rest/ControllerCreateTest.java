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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;


@ExtendWith(MockitoExtension.class)
class ControllerCreateTest {
    @Mock
    private ExamRepository examRepository;
    @Mock
    private SolutionAttemptRepository solutionAttemptRepository;
    @Mock
    private Principal principal;

    @Test
    void create() throws IOException {
        Controller target = new Controller(examRepository, solutionAttemptRepository);

        File examFile = new File("src/test/exams/simple.xml");
        MultipartFile file = new MockMultipartFile("test", new FileInputStream(examFile));
        Exam result = target.create(file, principal);

        Assertions.assertNotNull(result);
        Mockito.verify(examRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(examRepository);
    }
}
