package lizzy.medium.example.exams.web.exams;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lizzy.medium.example.exams.domain.Exam;
import lizzy.medium.example.exams.domain.ExamRepository;
import lizzy.medium.example.exams.domain.SolveResponse;
import lizzy.medium.example.exams.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
@Slf4j
class ExamsController {
    private final ExamRepository examRepository;
    private final ExamXmlReader examXmlReader;
    private final UserController userController;

    @Operation(summary = "Find all Exams", description = "Find all exams of the current user, including closed exams", operationId = "readAll")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content())
    @GetMapping(produces = {"application/json", "application/xml"})
    Collection<ExamDto> list() {
        User user = userController.getUser();
        return examRepository.findAllRunningOwnedBy(user).stream()
                .map(ExamDto::of)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Find single Exams", description = "Get a single (not closed) exam", operationId = "read")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content())
    @ApiResponse(responseCode = "404", description = "Exam does not exists or is closed", content = @Content())
    @GetMapping(value = "/{examId}", produces = {"application/json", "application/xml"})
    ExamDto get(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if (exam.isClosed()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return ExamDto.of(exam);
    }

    @Operation(summary = "Close an Exams", description = "Close a single (not closed) exam", operationId = "close")
    @ApiResponse(responseCode = "200", description = "Successful", content = @Content())
    @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content())
    @ApiResponse(responseCode = "403", description = "Not My Exam", content = @Content())
    @ApiResponse(responseCode = "404", description = "Exam does not exists or is closed", content = @Content())
    @DeleteMapping("/{examId}")
    void delete(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        User user = userController.getUser();
        if (!exam.isOwnedBy(user)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        if (exam.isClosed()) {
            return;
        }
        exam.close();
    }

    @Operation(summary = "Create an Exams", description = "Create a new exam by uploading an XML-File with the exam description", operationId = "create")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "400", description = "XML-File not valid", content = @Content())
    @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content())
    @PostMapping(produces = {"application/json", "application/xml"})
    ExamDto create(@Parameter(description = "An exam description file") @RequestParam("file") MultipartFile file) {
        User user = userController.getUser();
        try {
            Exam exam = examXmlReader.read(user, file.getInputStream());
            return ExamDto.of(exam);
        } catch (Exception e) {
            log.info("Could not parse XML-File", e);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot parse XML-File");
        }
    }

    @Operation(summary = "Try a solution", description = "Try a solution for a given exam, returns true or false depending if the check if successful", operationId = "check")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content())
    @ApiResponse(responseCode = "404", description = "Exam does not exists or is closed", content = @Content())
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The selected option for each question in the exam")
    @PostMapping(value = "/{examId}/solution", produces = {"application/json", "application/xml"})
    boolean trySolution(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId, @RequestBody SolutionDto solution) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        User user = userController.getUser();
        SolveResponse response = exam.solve(user, solution.getAnswers(), solution.getComment());
        if (response == SolveResponse.NO_ATTEMPTS_LEFT) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "No attempts left");
        }
        if (response == SolveResponse.WRONG_INPUT) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Wrong number of answers");
        }
        return response == SolveResponse.SUCCESSFUL;
    }

    @Operation(summary = "Get tries left", description = "Get the number of tries left for a given exam", operationId = "triesLeft")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content())
    @ApiResponse(responseCode = "404", description = "Exam does not exists or is closed", content = @Content())
    @GetMapping(value = "/{examId}/triesLeft", produces = {"application/json", "application/xml"})
    int triesLeft(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId) {
        User user = userController.getUser();
        return examRepository.findById(examId)
                .map(e -> e.getTriesLeft(user))
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Get participants", description = "Get the participants of a given exam", operationId = "participants")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content())
    @ApiResponse(responseCode = "403", description = "Not My Exam", content = @Content())
    @ApiResponse(responseCode = "404", description = "Exam does not exists or is closed", content = @Content())
    @GetMapping(value = "/{examId}/participants", produces = {"application/json", "application/xml"})
    List<ParticipationDto> participants(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        User user = userController.getUser();
        if (!exam.isOwnedBy(user)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        return exam.getParticipations().stream()
                .map(ParticipationDto::of)
                .collect(Collectors.toList());
    }
}
