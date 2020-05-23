package lizzy.medium.example.exams.domain;

import lizzy.medium.example.exams.domain.model.Exam;
import lizzy.medium.example.exams.domain.model.ExamRepository;
import lizzy.medium.example.exams.domain.services.ExamCloseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@Import(ExamCloseService.class)
public class ExamCloseServiceTest {

    @Autowired
    private ExamCloseService target;

    @MockBean
    private ExamRepository examRepository;

    @Test
    void close() {
        Exam exam = target.close(TestData.exam);

        Assertions.assertTrue(exam.isClosed());
        Mockito.verify(examRepository).update(exam);
    }
}
