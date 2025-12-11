package com.humidifier.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalExceptionHandler {

    public static void handleAppException(AppException ex) {
        log.error("Ошибка приложения: {}", ex.getMessage());
        System.err.println("Ошибка: " + ex.getMessage());
    }

    public static void handleUnexpectedError(Exception ex) {
        log.error("Неожиданная ошибка: {}", ex.toString());
        System.err.println("Произошла непредвиденная ошибка. Подробнее в логах.");
    }
}

