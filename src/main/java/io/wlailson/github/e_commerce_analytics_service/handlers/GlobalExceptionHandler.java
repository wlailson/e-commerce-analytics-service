package io.wlailson.github.e_commerce_analytics_service.handlers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception ex, HttpServletRequest request) {
        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatusCode status = errorResponse.getStatusCode();
            return problem(status, titleFor(status), detailFor(status), request);
        }

        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                "Ocorreu um erro inesperado ao processar a requisição.",
                request
        );
    }

    private ResponseEntity<ProblemDetail> problem(
            HttpStatusCode status,
            String title,
            String detail,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        return ResponseEntity.status(status).body(problem);
    }

    private String titleFor(HttpStatusCode status) {
        if (status.value() == HttpStatus.BAD_REQUEST.value()) {
            return "Requisição inválida";
        }
        if (status.value() == HttpStatus.NOT_FOUND.value()) {
            return "Recurso não encontrado";
        }
        if (status.is4xxClientError()) {
            return "Requisição não permitida";
        }
        return "Erro interno";
    }

    private String detailFor(HttpStatusCode status) {
        if (status.value() == HttpStatus.BAD_REQUEST.value()) {
            return "Verifique o formato e os parâmetros enviados.";
        }
        if (status.value() == HttpStatus.NOT_FOUND.value()) {
            return "O recurso solicitado não foi encontrado.";
        }
        if (status.is4xxClientError()) {
            return "A requisição não pôde ser processada.";
        }
        return "Ocorreu um erro inesperado ao processar a requisição.";
    }
}
