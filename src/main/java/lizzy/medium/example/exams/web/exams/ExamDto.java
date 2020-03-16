package lizzy.medium.example.exams.web.exams;

import lizzy.medium.example.exams.domain.Exam;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Value
@Builder
class ExamDto {
    private final UUID id;
    private final List<QuestionDto> questions;
    private final String title;
    private final String text;
    private final String owner;

    static ExamDto of(Exam exam) {
        List<QuestionDto> questions = exam.getQuestions().stream()
                .map(QuestionDto::of)
                .collect(Collectors.toList());
        return ExamDto.builder()
                .id(exam.getId())
                .text(exam.getText())
                .title(exam.getTitle())
                .owner(exam.getOwnerId())
                .questions(questions)
                .build();
    }
}