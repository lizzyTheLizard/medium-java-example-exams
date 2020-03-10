package lizzy.medium.security.exams.web.exams;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lizzy.medium.security.exams.domain.Exam;
import lizzy.medium.security.exams.domain.ExamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
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

    @Operation(summary = "Find all Exams", description = "Find all exams of the current user, including closed exams", operationId = "readAll")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content())
    @GetMapping(produces = {"application/json", "application/xml"})
    Collection<ExamDto> list(Principal principal) {
        return examRepository.findAllOwnedBy(principal.getName()).stream()
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
    void delete(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId, Principal principal) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if (!exam.isOwnedBy(principal)) {
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
    ExamDto create(@Parameter(description = "An exam description file") @RequestParam("file") MultipartFile file, Principal principal) {
        try {
            Exam exam = examXmlReader.read(principal, file.getInputStream());
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
    boolean trySolution(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId, @RequestBody List<Integer> answers, Principal principal) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if (!exam.hasAttemptsLeft(principal)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "To many attempts");
        }
        if (!(principal instanceof JwtAuthenticationToken)) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Wrong authentication");
        }
        Jwt jwt = ((JwtAuthenticationToken) principal).getToken();
        boolean result = exam.checkSolution(answers);
        exam.addSolutionAttempt(jwt.getClaimAsString("given_name"), jwt.getClaimAsString("family_name"),
                principal.getName(), result);
        return result;
    }

    @Operation(summary = "Get tries left", description = "Get the number of tries left for a given exam", operationId = "triesLeft")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content())
    @ApiResponse(responseCode = "404", description = "Exam does not exists or is closed", content = @Content())
    @GetMapping(value = "/{examId}/triesLeft", produces = {"application/json", "application/xml"})
    int triesLeft(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId, Principal principal) {
        return examRepository.findById(examId)
                .map(e -> e.getTriesLeft(principal))
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Get participants", description = "Get the participants of a given exam", operationId = "participants")
    @ApiResponse(responseCode = "200", description = "Successful")
    @ApiResponse(responseCode = "401", description = "Not Authenticated", content = @Content())
    @ApiResponse(responseCode = "403", description = "Not My Exam", content = @Content())
    @ApiResponse(responseCode = "404", description = "Exam does not exists or is closed", content = @Content())
    @GetMapping(value = "/{examId}/participants", produces = {"application/json", "application/xml"})
    List<ParticipantDto> participants(@Parameter(description = "ID of the exam") @PathVariable("examId") UUID examId, Principal principal) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if (!exam.isOwnedBy(principal)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        return exam.getParticipants().stream()
                .map(ParticipantDto::of)
                .collect(Collectors.toList());
    }
}
