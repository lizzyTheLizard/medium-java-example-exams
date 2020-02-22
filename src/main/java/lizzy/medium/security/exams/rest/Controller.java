package lizzy.medium.security.exams.rest;

import lizzy.medium.security.exams.helper.ExamXmlReader;
import lizzy.medium.security.exams.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
@Slf4j
class Controller {
    private final ExamRepository examRepository;
    private final SolutionAttemptRepository solutionAttemptRepository;

    /**
     * Return all the exams of the current user
     *
     * @param principal The current user
     * @return A list of all exams
     */
    @GetMapping
    Collection<Exam> list(Principal principal) {
        return examRepository.findByUser(principal);
    }

    /**
     * Get specific single exam
     *
     * @param examId The exam ID
     * @return The exam
     */
    //TODO This contains unsafe code, way to much information is returned to the UI
    @GetMapping("/{examId}")
    Exam get(@PathVariable("examId") UUID examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    /**
     * Delete a exam, only the owner of a exam should be able to do this
     *
     * @param examId    The exam ID
     * @param principal The current user
     */
    @DeleteMapping("/{examId}")
    void delete(@PathVariable("examId") UUID examId, Principal principal) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if (!exam.getOwner().equals(principal.getName())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        exam.setDeleted(true);
        examRepository.save(exam);
    }

    /**
     * Create a new exam by uploading a XML-File with the exam description
     *
     * @param file      The exam description
     * @param principal The current user
     * @return The created exam
     */
    @PostMapping()
    Exam create(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            ExamXmlReader xmlReader = new ExamXmlReader(file.getInputStream(), principal);
            Exam exam = xmlReader.read();
            examRepository.save(exam);
            return exam;
        } catch (Exception e) {
            log.info("Could not parse XML-File", e);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot parse XML-File");
        }
    }

    /**
     * Try out a given solution
     *
     * @param examId    The exam ID
     * @param answers   The solutions to tru
     * @param principal The current user
     * @return True if the solutions are correct, false otherwise
     */
    @PostMapping("/{examId}/solution")
    boolean trySolution(@PathVariable("examId") UUID examId, @RequestBody List<Integer> answers, Principal principal) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        ensureNotToManyAttempts(exam, principal);
        boolean success = checkIfAnswersAreCorrect(exam, answers);
        SolutionAttempt solutionAttempt = SolutionAttempt.builder()
                .exam(exam)
                .success(success)
                .participant(createParticipants(principal))
                .build();
        solutionAttemptRepository.save(solutionAttempt);
        return success;
    }

    private void ensureNotToManyAttempts(Exam exam, Principal principal) {
        int attempts = solutionAttemptRepository.countForExamAndUser(exam, principal);
        if (attempts >= exam.getMaxAttempts()) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "To many attempts");
        }
    }

    private boolean checkIfAnswersAreCorrect(Exam exam, List<Integer> answers) {
        List<Question> questions = exam.getQuestions();
        if (questions.size() != answers.size()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Wrong number of solutions");
        }
        return IntStream.range(0, questions.size())
                .noneMatch(i -> questions.get(i).getCorrectOption() != answers.get(i));
    }

    private Participant createParticipants(Principal principal) {
        if (!(principal instanceof JwtAuthenticationToken)) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Wrong authentication method");
        }
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        return Participant.builder()
                .firstName(token.getToken().getClaim("given_name"))
                .lastName(token.getToken().getClaim("family_name"))
                .userId(principal.getName())
                .build();
    }

    /**
     * Get number of tries left for the current user
     *
     * @param examId    The exam
     * @param principal The current user
     * @return True if the solutions are correct, false otherwise
     */
    @GetMapping("/{examId}/triesLeft")
    int triesLeft(@PathVariable("examId") UUID examId, Principal principal) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        int attempts = solutionAttemptRepository.countForExamAndUser(exam, principal);
        return exam.getMaxAttempts() - attempts;
    }

    /**
     * Get the participants of an exam, only the owner of a exam should be able to do this
     *
     * @param examId    The exam
     * @param principal The current user
     * @return A map of all participants and if they succeed (true), failed (false) or have open tries (null)
     */
    @GetMapping("/{examId}/participants")
    List<SuccessReturn> participants(@PathVariable("examId") UUID examId, Principal principal) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if (!exam.getOwner().equals(principal.getName())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        return solutionAttemptRepository.participantIdsByExam(exam).stream()
                .map(p -> getSuccessReturn(exam, p))
                .collect(Collectors.toList());
    }

    private SuccessReturn getSuccessReturn(Exam exam, String participantId) {
        List<SolutionAttempt> attempts = solutionAttemptRepository.findForParticipantAndExam(participantId, exam);
        if (attempts.stream().anyMatch(SolutionAttempt::isSuccess)) {
            return SuccessReturn.builder()
                    .participant(attempts.get(0).getParticipant())
                    .status(SuccessReturn.SuccessStatus.SUCCESS)
                    .build();
        }
        if (attempts.size() >= exam.getMaxAttempts()) {
            return SuccessReturn.builder()
                    .participant(attempts.get(0).getParticipant())
                    .status(SuccessReturn.SuccessStatus.FAILED)
                    .build();
        }
        return SuccessReturn.builder()
                .participant(attempts.get(0).getParticipant())
                .status(SuccessReturn.SuccessStatus.OPEN)
                .build();
    }
}
