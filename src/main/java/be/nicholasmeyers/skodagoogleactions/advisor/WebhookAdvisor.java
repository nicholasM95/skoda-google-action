package be.nicholasmeyers.skodagoogleactions.advisor;

import be.nicholasmeyers.skodagoogleactions.client.resource.ProblemDetailResponseResource;
import be.nicholasmeyers.skodagoogleactions.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class WebhookAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler({WebHookInputException.class})
    protected ResponseEntity<ProblemDetailResponseResource> handleException(WebHookInputException exception, HttpServletRequest request) {
        ProblemDetailResponseResource problemDetail = new ProblemDetailResponseResource("Invalid webhook request", 400, exception.getMessage(), request.getRequestURI());
        log.error(problemDetail.toString());
        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({KilometerException.class})
    protected ResponseEntity<ProblemDetailResponseResource> handleException(KilometerException exception, HttpServletRequest request) {
        ProblemDetailResponseResource problemDetail = new ProblemDetailResponseResource("Skoda Service unavailable", 503, exception.getMessage(), request.getRequestURI());
        log.error(problemDetail.toString());
        return new ResponseEntity<>(problemDetail, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler({CommandRequestException.class})
    protected ResponseEntity<ProblemDetailResponseResource> handleException(CommandRequestException exception, HttpServletRequest request) {
        ProblemDetailResponseResource problemDetail = new ProblemDetailResponseResource("Invalid webhook request", 400, exception.getMessage(), request.getRequestURI());
        log.error(problemDetail.toString());
        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({LocationException.class})
    protected ResponseEntity<ProblemDetailResponseResource> handleException(LocationException exception, HttpServletRequest request) {
        ProblemDetailResponseResource problemDetail = new ProblemDetailResponseResource("Skoda Service unavailable", 503, exception.getMessage(), request.getRequestURI());
        log.error(problemDetail.toString());
        return new ResponseEntity<>(problemDetail, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler({FlashException.class})
    protected ResponseEntity<ProblemDetailResponseResource> handleException(FlashException exception, HttpServletRequest request) {
        ProblemDetailResponseResource problemDetail = new ProblemDetailResponseResource("Skoda Service unavailable", 503, exception.getMessage(), request.getRequestURI());
        log.error(problemDetail.toString());
        return new ResponseEntity<>(problemDetail, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler({HonkException.class})
    protected ResponseEntity<ProblemDetailResponseResource> handleException(HonkException exception, HttpServletRequest request) {
        ProblemDetailResponseResource problemDetail = new ProblemDetailResponseResource("Skoda Service unavailable", 503, exception.getMessage(), request.getRequestURI());
        log.error(problemDetail.toString());
        return new ResponseEntity<>(problemDetail, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler({UnsupportedOperationException.class})
    protected ResponseEntity<ProblemDetailResponseResource> handleException(UnsupportedOperationException exception, HttpServletRequest request) {
        ProblemDetailResponseResource problemDetail = new ProblemDetailResponseResource("UnsupportedOperationException", 400, exception.getMessage(), request.getRequestURI());
        log.error(problemDetail.toString());
        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }
}
