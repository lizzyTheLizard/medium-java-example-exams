package lizzy.medium.security.exams.web.exams;

import lizzy.medium.security.exams.domain.Question;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
class QuestionDto {
    private final String text;
    private final List<String> options;

    static QuestionDto of(Question question) {
        return QuestionDto.builder()
                .text(question.getText())
                .options(question.getOptions())
                .build();
    }

}
