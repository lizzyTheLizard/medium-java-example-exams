package lizzy.medium.example.exams.web.api.exams;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
class SolutionDto {
    List<Integer> answers;
    String comment;
}