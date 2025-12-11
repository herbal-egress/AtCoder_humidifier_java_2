package com.humidifier.util;

import com.humidifier.exception.AppException;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class InputReader {

    private static final Scanner scanner = new Scanner(System.in);

    public static int readInt(String message) {
        System.out.print(message);
        if (!scanner.hasNextInt()) {
            throw new AppException("Ожидалось целое число.");
        }
        int value = scanner.nextInt();
        log.debug("Ввод int: {}", value);
        return value;
    }

    public static long readLong(String message) {
        System.out.print(message);
        if (!scanner.hasNextLong()) {
            throw new AppException("Ожидалось целое число (long).");
        }
        long value = scanner.nextLong();
        log.debug("Ввод long: {}", value);
        return value;
    }
}
