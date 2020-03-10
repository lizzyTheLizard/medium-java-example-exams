package lizzy.medium.security.exams.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ExamGetParticipantsTest extends ExamCloseTestBase {
    @Test
    void getParticipants() {
        Exam target = examBuilder.build();
        List<SolutionAttempt> solutionAttempts = new LinkedList<>();
        solutionAttempts.add(SolutionAttempt.builder()
                .id(UUID.randomUUID())
                .firstName("first")
                .lastName("last")
                .userId("user")
                .build());
        solutionAttempts.add(SolutionAttempt.builder()
                .id(UUID.randomUUID())
                .firstName("first2")
                .lastName("last2")
                .userId("user2")
                .build());
        Mockito.when(solutionAttemptRepository.findForExam(target)).thenReturn(solutionAttempts);

        List<Participant> participants = target.getParticipants();

        Assertions.assertEquals(2, participants.size());
        Participant participant1 = participants.get(1);
        Assertions.assertEquals("user", participant1.getUserId());
        Participant participant2 = participants.get(0);
        Assertions.assertEquals("user2", participant2.getUserId());
    }

    @Test
    void getSuccessful() {
        Exam target = examBuilder.maxAttempts(2).build();
        List<SolutionAttempt> solutionAttempts = new LinkedList<>();
        solutionAttempts.add(SolutionAttempt.builder()
                .id(UUID.randomUUID())
                .firstName("first")
                .lastName("last")
                .userId("user")
                .success(false)
                .build());
        solutionAttempts.add(SolutionAttempt.builder()
                .id(UUID.randomUUID())
                .firstName("first")
                .lastName("last")
                .userId("user")
                .success(true)
                .build());
        Mockito.when(solutionAttemptRepository.findForExam(target)).thenReturn(solutionAttempts);

        List<Participant> participants = target.getParticipants();
        Participant participant = participants.get(0);
        Assertions.assertEquals(ParticipantStatus.SUCCESS, participant.getStatus());
    }

    @Test
    void getFailed() {
        Exam target = examBuilder.maxAttempts(2).build();
        List<SolutionAttempt> solutionAttempts = new LinkedList<>();
        solutionAttempts.add(SolutionAttempt.builder()
                .id(UUID.randomUUID())
                .firstName("first")
                .lastName("last")
                .userId("user")
                .success(false)
                .build());
        solutionAttempts.add(SolutionAttempt.builder()
                .id(UUID.randomUUID())
                .firstName("first")
                .lastName("last")
                .userId("user")
                .success(false)
                .build());
        Mockito.when(solutionAttemptRepository.findForExam(target)).thenReturn(solutionAttempts);

        List<Participant> participants = target.getParticipants();
        Participant participant = participants.get(0);
        Assertions.assertEquals(ParticipantStatus.FAILED, participant.getStatus());
    }

    @Test
    void getOpen() {
        Exam target = examBuilder.maxAttempts(2).build();
        List<SolutionAttempt> solutionAttempts = new LinkedList<>();
        solutionAttempts.add(SolutionAttempt.builder()
                .id(UUID.randomUUID())
                .firstName("first")
                .lastName("last")
                .userId("user")
                .success(false)
                .build());
        Mockito.when(solutionAttemptRepository.findForExam(target)).thenReturn(solutionAttempts);

        List<Participant> participants = target.getParticipants();
        Participant participant = participants.get(0);
        Assertions.assertEquals(ParticipantStatus.OPEN, participant.getStatus());
    }
}
