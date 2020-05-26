package lizzy.medium.example.exams.web.api.exams;

import lizzy.medium.example.exams.domain.model.Exam;
import lizzy.medium.example.exams.domain.model.Question;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Value
@Builder
class ExamDto {
    UUID id;
    List<QuestionDto> questions;
    String title;
    String text;
    String owner;

    static ExamDto of(Exam exam, Collection<Question> questions) {
        List<QuestionDto> questionDtos = questions.stream()
                .map(QuestionDto::of)
                .collect(Collectors.toList());
        return ExamDto.builder()
                .id(exam.getId())
                .text(exam.getText())
                .title(exam.getTitle())
                .owner(exam.getOwnerId())
                .questions(questionDtos)
                .build();
    }
}