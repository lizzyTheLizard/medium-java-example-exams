package lizzy.medium.security.exams.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ExamCloseTest extends ExamCloseTestBase {

    @Test
    void close() {
        Exam target = examBuilder.build();

        target.close();

        Assertions.assertTrue(target.isClosed());
        Mockito.verify(examRepository).update(target);
    }
}
