package com.humidifier.controller;

import com.humidifier.domain.RefillEvent;
import com.humidifier.exception.AppException;
import com.humidifier.exception.GlobalExceptionHandler;
import com.humidifier.service.HumidifierService;
import com.humidifier.util.InputReader;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class ConsoleController {
    private final HumidifierService service = new HumidifierService();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        boolean playAgain = true;

        while (playAgain) {
            try {
                playOneGame();
            } catch (Exception ex) {
                GlobalExceptionHandler.handleUnexpectedError(ex);
            }

            // Запрос на повторную игру
            System.out.print("Ещё поиграем? (да/нет): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            playAgain = answer.equals("да") || answer.equals("yes") || answer.equals("д");
        }

        log.info("Приложение завершено.");
        scanner.close();
    }

    private void playOneGame() {
        log.info("=== НАЧАЛО НОВОЙ ИГРЫ ===");

        int N = InputReader.readInt("Введите количество доливов N: ");
        List<RefillEvent> events = new ArrayList<>();
        long previousTime = 0;
        long currentWater = 0;

        // Первый долив без расчёта утечки
        if (N > 0) {
            long firstT = InputReader.readLong("Введите T_1: ");
            long firstV = InputReader.readLong("Введите V_1: ");

            currentWater = firstV;
            events.add(new RefillEvent(firstT, firstV));
            previousTime = firstT;
            log.info("Добавлено {} л. Текущий уровень: {} л.", firstV, currentWater);
        }

        // Последующие доливы
        for (int i = 2; i <= N; i++) {
            boolean validInput = false;
            long T = 0;

            // Цикл повторного запроса времени
            while (!validInput) {
                try {
                    T = InputReader.readLong("Введите T_" + i + ": ");
                    if (T <= previousTime) {
                        throw new AppException("Время T_" + i + " должно быть больше предыдущего (" + previousTime + ")");
                    }
                    validInput = true;
                } catch (AppException ex) {
                    System.err.println("Ошибка: " + ex.getMessage());
                    System.err.println("Пожалуйста, введите корректное время.");
                }
            }

            // Расчёт утечки
            long hoursPassed = T - previousTime;
            long leak = Math.min(currentWater, hoursPassed);
            currentWater = currentWater - leak;

            // Вывод информации
            System.out.printf("Прошло %d часа(ов), утекло %d литра(ов) воды, осталось %d литра(ов) воды. Сколько добавить воды?%n",
                    hoursPassed, leak, currentWater);

            // Ввод объёма
            long V = InputReader.readLong("");

            // Добавление воды
            currentWater += V;
            events.add(new RefillEvent(T, V));
            previousTime = T;

            log.info("Добавлено {} л. Текущий уровень: {} л.", V, currentWater);
        }

        // Финальный результат
        if (!events.isEmpty()) {
            long result = service.calculateWaterAfterLastRefill(events);
            log.info("Итоговое количество воды: {} литров.", result);
            System.out.println("Итоговый результат: " + result);
        } else {
            System.out.println("Доливов не было.");
        }
    }
}