package lkeleti.redditclone.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@RestControllerAdvice
public class GlobalExceptionHandler implements ProblemHandling{
    @ExceptionHandler
    public ResponseEntity<Problem> handle(AbstractThrowableProblem e, NativeWebRequest request) {
        ThrowableProblem problem = Problem.builder().withStatus(e.getStatus())
                .with("URI",e.getType())
                .withTitle(e.getTitle())
                .withDetail(e.getDetail())
                .build();
        return create(problem,request);
    }
}

