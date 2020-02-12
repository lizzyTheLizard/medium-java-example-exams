package izzy.medium.security.exams.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@RestController
@RequestMapping("riddle")
@RequiredArgsConstructor
@Slf4j
class Controller {
    private final RiddleRepository riddleRepository;

    /**
     * Return all the riddles of the current user
     * @param principal The current user
     * @return A list of all riddles
     */
    @GetMapping
    Collection<Riddle> list(Principal principal) {
        return riddleRepository.findByUser(principal);
    }

    /**
     * Get specific single riddle
     * @param riddleId The riddle ID
     * @return The riddle
     */
    @GetMapping("/{riddle}")
    Riddle get(@PathVariable("riddle") UUID riddleId) {
        return riddleRepository.findById(riddleId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    /**
     * Delete a riddle, only the owner of a riddle should be able to do this
     * @param riddleId The riddle ID
     * @param principal The current user
     */
    @DeleteMapping("/{riddle}")
    void delete(@PathVariable("riddle") UUID riddleId, Principal principal) {
        Riddle riddle = riddleRepository.findById(riddleId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if(!riddle.getOwner().equals(principal.getName())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        riddleRepository.delete(riddle);
    }

    /**
     * Create a new riddle by uploading a XML-File with the riddle description
     * @param file The riddle description
     * @param principal The current user
     * @return The created riddle
     */
    @PostMapping()
    Riddle create(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            RiddleXmlReader xmlReader = new RiddleXmlReader(file.getInputStream(), principal);
            Riddle riddle = xmlReader.read();
            riddleRepository.save(riddle);
            return riddle;
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Cannot parse XML-File");
        }
    }

    /**
     * Try out a given solution
     * @param answers The solutions to tru
     * @param principal The current user
     * @return True if the solutions are correct, false otherwise
     */
    @PostMapping("/{riddle}/solution")
    boolean trySolution(@PathVariable("riddle") UUID riddleId, @RequestBody List<Integer> answers, Principal principal) {
        Riddle riddle = riddleRepository.findById(riddleId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        List<Question> questions = riddle.getQuestions();
        if(questions.size() != answers.size()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Wrong number of solutions");
        }
        boolean result = IntStream.range(0, questions.size())
                .noneMatch(i -> questions.get(i).getCorrectSolution() != answers.get(i));

        if(result) {
            riddle.addSolver(principal.getName());
            riddleRepository.save(riddle);
        }
        return result;
    }
}
