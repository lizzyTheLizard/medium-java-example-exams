package lizzy.medium.example.exams.web.api.exams;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lizzy.medium.example.exams.domain.model.Exam;
import lizzy.medium.example.exams.domain.model.User;
import lizzy.medium.example.exams.domain.services.ExamCloseService;
import lizzy.medium.example.exams.domain.services.ExamService;
import lizzy.medium.example.exams.domain.services.ExamSolveService;
import lizzy.medium.example.exams.domain.services.ExamXmlReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@ApiResponse(responseCode = "200", description = "Successful")
@ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content())
@Slf4j
class ExamsController {
    private final ExamService examService;
    private final ExamCloseService examCloseService;
    private final ExamXmlReaderService examXmlReaderService;
    private final ExamSolveService examSolveService;

    @Operation(summary = "Find all Exams", description = "Find all exams of the current user, including closed exams", operationId = "readAll")
    @GetMapping(produces = {"application/json"})
    Collection<ExamDto> list() {
        User user = getUser();
        return examService.findAllRunningOwnedBy(user).stream()
                .map(exam -> ExamDto.of(exam, examService.getQuestions(exam)))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Find single Exams", description = "Get a single (not closed) exam", operationId = "read")
    @ApiResponse(responseCode = "404", description = "Exam does not exists or is closed", content = @Content())
    @GetMapping(value = "/{examId}", produces = {"application/json"})
    ExamDto get(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId) {
        Exam exam = examService.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if (exam.isClosed()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return ExamDto.of(exam, examService.getQuestions(exam));
    }

    @Operation(summary = "Close an Exams", description = "Close a single (not closed) exam", operationId = "close")
    @ApiResponse(responseCode = "403", description = "Not My Exam", content = @Content())
    @ApiResponse(responseCode = "404", description = "Exam does not exists or is closed", content = @Content())
    @DeleteMapping("/{examId}")
    void delete(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId) {
        Exam exam = examService.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        User user = getUser();
        if (!exam.isOwnedBy(user)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        if (exam.isClosed()) {
            return;
        }
        examCloseService.close(exam);
    }

    @Operation(summary = "Create an Exams", description = "Create a new exam by uploading an XML-File with the exam description", operationId = "create")
    @ApiResponse(responseCode = "400", description = "XML-File not valid", content = @Content())
    @PostMapping(produces = {"application/json"})
    ExamDto create(@Parameter(description = "An exam description file") @RequestParam("file") MultipartFile file) {
        User user = getUser();
        try {
            Exam exam = examXmlReaderService.read(user, file.getInputStream());
            return ExamDto.of(exam, examService.getQuestions(exam));
        } catch (Exception e) {
            log.info("Could not parse XML-File", e);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot parse XML-File");
        }
    }

    @Operation(summary = "Try a solution", description = "Try a solution for a given exam, returns true or false depending if the check if successful", operationId = "check")
    @ApiResponse(responseCode = "404", description = "Exam does not exists or is closed", content = @Content())
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The selected option for each question in the exam")
    @PostMapping(value = "/{examId}/solution", produces = {"application/json"})
    boolean trySolution(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId, @RequestBody SolutionDto solution) {
        Exam exam = examService.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        User user = getUser();
        ExamSolveService.SolveResponse response = examSolveService.solve(exam, user, solution.getAnswers(), solution.getComment());
        if (response == ExamSolveService.SolveResponse.NO_ATTEMPTS_LEFT) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "No attempts left");
        }
        if (response == ExamSolveService.SolveResponse.WRONG_INPUT) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Wrong number of answers");
        }
        return response == ExamSolveService.SolveResponse.SUCCESSFUL;
    }

    @Operation(summary = "Get tries left", description = "Get the number of tries left for a given exam", operationId = "triesLeft")
    @ApiResponse(responseCode = "404", description = "Exam does not exists or is closed", content = @Content())
    @GetMapping(value = "/{examId}/triesLeft", produces = {"application/json"})
    int triesLeft(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId) {
        User user = getUser();
        return examService.findById(examId)
                .map(exam -> examSolveService.getTriesLeft(exam, user))
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Get participants", description = "Get the participants of a given exam", operationId = "participants")
    @ApiResponse(responseCode = "403", description = "Not My Exam", content = @Content())
    @ApiResponse(responseCode = "404", description = "Exam does not exists or is closed", content = @Content())
    @GetMapping(value = "/{examId}/participants", produces = {"application/json"})
    List<ParticipationDto> participants(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId) {
        Exam exam = examService.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        User user = getUser();
        if (!exam.isOwnedBy(user)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        return examService.getMostRecentParticipationForeachUser(exam).stream()
                .map(ParticipationDto::of)
                .collect(Collectors.toList());
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "You need to be logged in");
        }
        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Wrong authentication");
        }
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        Jwt jwt = jwtAuthenticationToken.getToken();
        return User.builder()
                .id(jwt.getSubject())
                .firstName(jwt.getClaimAsString("given_name"))
                .lastName(jwt.getClaimAsString("family_name"))
                .build();
    }
}
