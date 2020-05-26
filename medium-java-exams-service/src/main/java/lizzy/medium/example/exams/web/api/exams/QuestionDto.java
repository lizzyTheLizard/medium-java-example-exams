package lizzy.medium.example.exams.web.api.exams;

import lizzy.medium.example.exams.domain.model.Question;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
class QuestionDto {
    String text;
    List<String> options;

    static QuestionDto of(Question question) {
        return QuestionDto.builder()
                .text(question.getText())
                .options(question.getOptions())
                .build();
    }

}
