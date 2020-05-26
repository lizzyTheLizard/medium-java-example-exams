package lizzy.medium.example.exams.domain;

import lizzy.medium.example.exams.domain.model.*;
import lizzy.medium.example.exams.domain.services.ExamService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@Import(ExamService.class)
public class ExamServiceTest {
    @Autowired
    private ExamService target;

    @MockBean
    private ExamRepository examRepository;

    @MockBean
    private QuestionRepository questionRepository;

    @MockBean
    private ParticipationRepository participationRepository;

    @Test
    void getQuestions() {
        List<Question> questions = List.of(TestData.question);
        Mockito.when(questionRepository.getQuestions(TestData.exam)).thenReturn(questions);

        Collection<Question> result = target.getQuestions(TestData.exam);

        Assertions.assertIterableEquals(questions, result);
    }

    @Test
    void getMostRecentParticipationForeachUser() {
        List<Participation> participations = List.of(TestData.participation);
        Mockito.when(participationRepository.getMostRecentForeachUser(TestData.exam)).thenReturn(participations);

        Collection<Participation> result = target.getMostRecentParticipationForeachUser(TestData.exam);

        Assertions.assertIterableEquals(participations, result);
    }

    @Test
    void findById() {
        Mockito.when(examRepository.findById(TestData.exam.getId())).thenReturn(Optional.of(TestData.exam));

        Exam result = target.findById(TestData.exam.getId()).orElseThrow(AssertionFailedError::new);

        Assertions.assertEquals(TestData.exam, result);
    }

    @Test
    void findAllRunningOwnedBy() {
        List<Exam> exams = List.of(TestData.exam);
        Mockito.when(examRepository.findAllRunningOwnedBy(TestData.user)).thenReturn(exams);

        Collection<Exam> result = target.findAllRunningOwnedBy(TestData.user);

        Assertions.assertIterableEquals(exams, result);
    }
}
