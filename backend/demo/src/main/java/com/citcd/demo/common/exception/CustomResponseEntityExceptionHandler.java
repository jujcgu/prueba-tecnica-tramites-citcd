package com.citcd.demo.common.exception;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final URI TYPE_VALIDATION = URI.create("urn:problem:validation");
    private static final URI TYPE_NOT_FOUND = URI.create("urn:problem:not-found");
    private static final URI TYPE_BAD_REQUEST = URI.create("urn:problem:bad-request");
    private static final URI TYPE_CONFLICT = URI.create("urn:problem:conflict");
    private static final URI TYPE_INTERNAL = URI.create("urn:problem:internal-error");

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle("Validation failed");
        pd.setType(TYPE_VALIDATION);
        pd.setDetail("Request body has validation errors.");

        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "rejectedValue", fe.getRejectedValue(),
                        "message", fe.getDefaultMessage()))
                .toList();
        pd.setProperty("errors", errors);
        pd.setProperty("timestamp", OffsetDateTime.now().toString());

        setInstanceFromRequest(pd, request);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest req) {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setType(TYPE_VALIDATION);
        pd.setDetail("Request parameters have validation errors.");
        pd.setProperty("violations", ex.getConstraintViolations().stream()
                .map(v -> Map.of(
                        "path", String.valueOf(v.getPropertyPath()),
                        "invalidValue", v.getInvalidValue(),
                        "message", v.getMessage()))
                .toList());
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setInstance(URI.create(req.getRequestURI()));

        return ResponseEntity.status(pd.getStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest req) {

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Resource not found");
        pd.setType(TYPE_NOT_FOUND);
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setInstance(URI.create(req.getRequestURI()));

        return ResponseEntity.status(pd.getStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest req) {

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Bad request");
        pd.setType(TYPE_BAD_REQUEST);
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setInstance(URI.create(req.getRequestURI()));

        return ResponseEntity.status(pd.getStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(
            ResponseStatusException ex, HttpServletRequest req) {

        HttpStatusCode code = ex.getStatusCode();
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(code,
                ex.getReason() != null ? ex.getReason() : "Request failed");
        pd.setTitle(code.toString());
        pd.setType(TYPE_BAD_REQUEST);
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setInstance(URI.create(req.getRequestURI()));

        return ResponseEntity.status(code)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest req) {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Data integrity violation");
        pd.setType(TYPE_CONFLICT);
        pd.setDetail("The request conflicts with current server state (e.g., duplicate key).");
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setInstance(URI.create(req.getRequestURI()));

        return ResponseEntity.status(pd.getStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ProblemDetail> handleErrorResponseException(
            ErrorResponseException ex, HttpServletRequest req) {

        ProblemDetail pd = ex.getBody();
        if (pd.getInstance() == null) {
            pd.setInstance(URI.create(req.getRequestURI()));
        }
        pd.setProperty("timestamp", OffsetDateTime.now().toString());

        return ResponseEntity.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(
            Exception ex, HttpServletRequest req) {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Unexpected error");
        pd.setType(TYPE_INTERNAL);
        pd.setDetail("An unexpected error occurred.");
        pd.setProperty("timestamp", OffsetDateTime.now().toString());
        pd.setInstance(URI.create(req.getRequestURI()));

        return ResponseEntity.status(pd.getStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }

    private static void setInstanceFromRequest(ProblemDetail pd, WebRequest request) {
        if (request instanceof ServletWebRequest swr) {
            pd.setInstance(URI.create(swr.getRequest().getRequestURI()));
        }
    }
}
