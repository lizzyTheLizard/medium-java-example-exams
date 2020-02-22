package lizzy.medium.security.exams.rest;

import lizzy.medium.security.exams.model.Participant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class SuccessReturn {
    private final Participant participant;
    private final SuccessStatus status;

    enum SuccessStatus {
        FAILED, OPEN, SUCCESS
    }
}