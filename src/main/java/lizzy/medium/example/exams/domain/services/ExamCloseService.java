package lizzy.medium.example.exams.domain.services;

import lizzy.medium.example.exams.domain.model.Exam;
import lizzy.medium.example.exams.domain.model.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExamCloseService {
    private final ExamRepository examRepository;

    public Exam close(Exam exam) {
        Exam newExam = exam.toBuilder().closed(true).build();
        examRepository.update(newExam);
        return newExam;
    }
}
