package com.adbrassacoma.administrativo.infrastructure.validator;

public class CpfValidator {

    public static boolean isValid(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return false;
        }
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            return false;
        }
        if (isAllDigitsEqual(cpfLimpo)) {
            return false;
        }
        return validateDigits(cpfLimpo);
    }

    private static boolean isAllDigitsEqual(String cpf) {
        char firstDigit = cpf.charAt(0);
        return cpf.chars().allMatch(c -> c == firstDigit);
    }

    private static boolean validateDigits(String cpf) {
        int[] digits = cpf.chars().map(Character::getNumericValue).toArray();

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += digits[i] * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) {
            firstDigit = 0;
        }
        if (firstDigit != digits[9]) {
            return false;
        }
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) {
            secondDigit = 0;
        }
        return secondDigit == digits[10];
    }

    public static String format(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return null;
        }
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            return null;
        }
        return String.format("%s.%s.%s-%s",
                cpfLimpo.substring(0, 3),
                cpfLimpo.substring(3, 6),
                cpfLimpo.substring(6, 9),
                cpfLimpo.substring(9, 11));
    }

    public static String unformat(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return null;
        }
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            return null;
        }
        return cpfLimpo;
    }
}
