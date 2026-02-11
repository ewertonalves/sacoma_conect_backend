package com.adbrassacoma.administrativo.infrastructure.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleEmailJaCadastradoExceptionDeveRetornar409() {
        EmailJaCadastradoException ex = new EmailJaCadastradoException("Email já cadastrado");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleEmailJaCadastradoException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
        assertEquals("Email já cadastrado", response.getBody().error());
    }

    @Test
    void handleCredenciaisInvalidasExceptionDeveRetornar401() {
        CredenciaisInvalidasException ex = new CredenciaisInvalidasException("Credenciais inválidas");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleCredenciaisInvalidasException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().status());
    }

    @Test
    void handleBadCredentialsExceptionDeveRetornar401() {
        BadCredentialsException ex = new BadCredentialsException("bad");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleBadCredentialsException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().message().contains("incorretos"));
    }

    @Test
    void handleAccessDeniedExceptionDeveRetornar403() {
        AccessDeniedException ex = new AccessDeniedException("denied");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleAccessDeniedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().status());
    }

    @Test
    void handleUsuarioNaoEncontradoExceptionDeveRetornar404() {
        UsuarioNaoEncontradoException ex = new UsuarioNaoEncontradoException("Usuário não encontrado");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleUsuarioNaoEncontradoException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuário não encontrado", response.getBody().message());
    }

    @Test
    void handleMembroNaoEncontradoExceptionDeveRetornar404() {
        MembroNaoEncontradoException ex = new MembroNaoEncontradoException("Membro não encontrado");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleMembroNaoEncontradoException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleCepNaoEncontradoExceptionDeveRetornar404() {
        CepNaoEncontradoException ex = new CepNaoEncontradoException("CEP não encontrado");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleCepNaoEncontradoException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleFinanceiroNaoEncontradoExceptionDeveRetornar404() {
        FinanceiroNaoEncontradoException ex = new FinanceiroNaoEncontradoException("Não encontrado");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleFinanceiroNaoEncontradoException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleAssistenciaSocialNaoEncontradoExceptionDeveRetornar404() {
        AssistenciaSocialNaoEncontradoException ex = new AssistenciaSocialNaoEncontradoException("Não encontrado");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleAssistenciaSocialNaoEncontradoException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleCpfInvalidoExceptionDeveRetornar400() {
        CpfInvalidoException ex = new CpfInvalidoException("CPF inválido");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleCpfInvalidoException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleGenericExceptionDeveRetornar500() {
        Exception ex = new RuntimeException("Erro inesperado");
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().status());
        assertEquals("Erro inesperado", response.getBody().message());
    }

    @Test
    void handleValidationExceptionsDeveRetornar400ComErros() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(new FieldError("request", "email", "Email inválido")));

        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("errors"));
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals("Email inválido", errors.get("email"));
    }
}
