package lizzy.medium.security.exams.rest;

import lizzy.medium.security.exams.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpStatusCodeException;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class ControllerGetParticipantsInformationTest {
    @Mock
    private ExamRepository examRepository;
    @Mock
    private SolutionAttemptRepository solutionAttemptRepository;
    @Mock
    private Exam exam;
    @Mock
    private Principal principal;
    @Mock
    private Participant participant;
    @Mock
    private SolutionAttempt solutionAttempt;

    @Test
    void triesLeft() {
        UUID id = UUID.randomUUID();
        Controller target = new Controller(examRepository, solutionAttemptRepository);
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(exam.getMaxAttempts()).thenReturn(2);
        Mockito.when(solutionAttemptRepository.countForExamAndUser(exam, principal)).thenReturn(1);

        int result = target.triesLeft(id, principal);

        Assertions.assertEquals(1, result);
    }

    @Test
    void participantSuccessful() {
        UUID id = UUID.randomUUID();
        Controller target = new Controller(examRepository, solutionAttemptRepository);
        Mockito.when(exam.getOwner()).thenReturn("test");
        Mockito.when(principal.getName()).thenReturn("test");
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(solutionAttemptRepository.participantIdsByExam(exam)).thenReturn(Collections.singletonList("test123"));
        Mockito.when(solutionAttemptRepository.findForParticipantAndExam("test123", exam)).thenReturn(Collections.singletonList(solutionAttempt));
        Mockito.when(solutionAttempt.isSuccess()).thenReturn(true);
        Mockito.when(solutionAttempt.getParticipant()).thenReturn(participant);

        List<SuccessReturn> result = target.participants(id, principal);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(participant, result.get(0).getParticipant());
        Assertions.assertEquals(SuccessReturn.SuccessStatus.SUCCESS, result.get(0).getStatus());
    }

    @Test
    void participantFailed() {
        UUID id = UUID.randomUUID();
        Controller target = new Controller(examRepository, solutionAttemptRepository);
        Mockito.when(exam.getOwner()).thenReturn("test");
        Mockito.when(exam.getMaxAttempts()).thenReturn(1);
        Mockito.when(principal.getName()).thenReturn("test");
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(solutionAttemptRepository.participantIdsByExam(exam)).thenReturn(Collections.singletonList("test123"));
        Mockito.when(solutionAttemptRepository.findForParticipantAndExam("test123", exam)).thenReturn(Collections.singletonList(solutionAttempt));
        Mockito.when(solutionAttempt.isSuccess()).thenReturn(false);
        Mockito.when(solutionAttempt.getParticipant()).thenReturn(participant);

        List<SuccessReturn> result = target.participants(id, principal);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(participant, result.get(0).getParticipant());
        Assertions.assertEquals(SuccessReturn.SuccessStatus.FAILED, result.get(0).getStatus());
    }

    @Test
    void participantOpen() {
        UUID id = UUID.randomUUID();
        Controller target = new Controller(examRepository, solutionAttemptRepository);
        Mockito.when(exam.getOwner()).thenReturn("test");
        Mockito.when(exam.getMaxAttempts()).thenReturn(2);
        Mockito.when(principal.getName()).thenReturn("test");
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));
        Mockito.when(solutionAttemptRepository.participantIdsByExam(exam)).thenReturn(Collections.singletonList("test123"));
        Mockito.when(solutionAttemptRepository.findForParticipantAndExam("test123", exam)).thenReturn(Collections.singletonList(solutionAttempt));
        Mockito.when(solutionAttempt.isSuccess()).thenReturn(false);
        Mockito.when(solutionAttempt.getParticipant()).thenReturn(participant);

        List<SuccessReturn> result = target.participants(id, principal);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(participant, result.get(0).getParticipant());
        Assertions.assertEquals(SuccessReturn.SuccessStatus.OPEN, result.get(0).getStatus());
    }

    @Test
    void participantNotAllowed() {
        UUID id = UUID.randomUUID();
        Controller target = new Controller(examRepository, solutionAttemptRepository);
        Mockito.when(exam.getOwner()).thenReturn("test");
        Mockito.when(principal.getName()).thenReturn("other");
        Mockito.when(examRepository.findById(id)).thenReturn(Optional.of(exam));

        Assertions.assertThrows(HttpStatusCodeException.class, () -> target.participants(id, principal));
    }
}
