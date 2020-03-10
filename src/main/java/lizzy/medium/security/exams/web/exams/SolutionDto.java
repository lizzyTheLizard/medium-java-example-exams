package lizzy.medium.security.exams.web.exams;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
class SolutionDto {
    private final List<Integer> answers;
    private final String comment;
}