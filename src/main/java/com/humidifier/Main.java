package com.humidifier;

import com.humidifier.controller.ConsoleController;
import com.humidifier.exception.GlobalExceptionHandler;

public class Main {
    public static void main(String[] args) {
        try {
            new ConsoleController().start();
        } catch (Exception ex) {
            GlobalExceptionHandler.handleUnexpectedError(ex);
        }
    }
}
