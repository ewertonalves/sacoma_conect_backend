package com.adbrassacoma.administrativo.infrastructure.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CpfValidator")
class CpfValidatorTest {

    @Nested
    @DisplayName("isValid")
    class IsValid {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        void deveRetornarFalsoQuandoNuloOuEmBranco(String cpf) {
            assertFalse(CpfValidator.isValid(cpf));
        }

        @Test
        void deveRetornarFalsoQuandoMenosDe11Digitos() {
            assertFalse(CpfValidator.isValid("1234567890"));
            assertFalse(CpfValidator.isValid("123"));
        }

        @Test
        void deveRetornarFalsoQuandoMaisDe11Digitos() {
            assertFalse(CpfValidator.isValid("123456789012"));
        }

        @Test
        void deveRetornarFalsoQuandoTodosDigitosIguais() {
            assertFalse(CpfValidator.isValid("11111111111"));
            assertFalse(CpfValidator.isValid("00000000000"));
        }

        @Test
        void deveRetornarFalsoQuandoDigitosVerificadoresInvalidos() {
            assertFalse(CpfValidator.isValid("12345678901"));
        }

        @Test
        void deveRetornarVerdadeiroQuandoCpfValido() {
            assertTrue(CpfValidator.isValid("111.444.777-35"));
            assertTrue(CpfValidator.isValid("11144477735"));
        }

        @Test
        void deveAceitarCpfFormatadoComPontosETraco() {
            assertTrue(CpfValidator.isValid("111.444.777-35"));
        }
    }

    @Nested
    @DisplayName("format")
    class Format {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void deveRetornarNuloQuandoNuloOuEmBranco(String cpf) {
            assertNull(CpfValidator.format(cpf));
        }

        @Test
        void deveRetornarNuloQuandoMenosDe11Digitos() {
            assertNull(CpfValidator.format("1234567890"));
        }

        @Test
        void deveFormatarCpfComPontosETraco() {
            assertEquals("111.444.777-35", CpfValidator.format("11144477735"));
            assertEquals("111.444.777-35", CpfValidator.format("111.444.777-35"));
        }
    }

    @Nested
    @DisplayName("unformat")
    class Unformat {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void deveRetornarNuloQuandoNuloOuEmBranco(String cpf) {
            assertNull(CpfValidator.unformat(cpf));
        }

        @Test
        void deveRetornarNuloQuandoMenosDe11Digitos() {
            assertNull(CpfValidator.unformat("123"));
        }

        @Test
        void deveRemoverFormatacao() {
            assertEquals("11144477735", CpfValidator.unformat("111.444.777-35"));
            assertEquals("11144477735", CpfValidator.unformat("11144477735"));
        }
    }
}
