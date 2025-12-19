package com.humidifier.controller;

import com.humidifier.domain.RefillEvent;
import com.humidifier.exception.AppException;
import com.humidifier.exception.GlobalExceptionHandler;
import com.humidifier.service.HumidifierService;
import com.humidifier.util.InputReader;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConsoleController {
    private final HumidifierService service = new HumidifierService();
    private long previousTime = 0;
    private long currentWater = 0;

    public void start() {
        try {
            log.info("Запуск консольного интерфейса.");
            int N = InputReader.readInt("Введите количество доливов N: ");
            List<RefillEvent> events = new ArrayList<>();

            // Первый долив без расчёта утечки
            if (N > 0) {
                long firstT = InputReader.readLong("Введите T_1: ");
                long firstV = InputReader.readLong("Введите V_1: ");

                currentWater = firstV;
                events.add(new RefillEvent(firstT, firstV));
                previousTime = firstT;
                log.info("Добавлено {} л. Текущий уровень: {} л.", firstV, currentWater);
            }

            // Последующие доливы с расчётом утечки
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
                        validInput = true; // Ввод корректен
                    } catch (AppException ex) {
                        // Выводим ошибку и продолжаем цикл
                        System.err.println("Ошибка: " + ex.getMessage());
                        System.err.println("Пожалуйста, введите корректное время.");
                    }
                }

                // Расчёт утечки (только после успешного ввода времени)
                long hoursPassed = T - previousTime;
                long leak = Math.min(currentWater, hoursPassed);
                currentWater = currentWater - leak;

                // Вывод информации об утечке и запрос добавления воды
                System.out.printf("Прошло %d часа(ов), утекло %d литра(ов) воды, осталось %d литра(ов) воды. Сколько добавить воды?%n",
                        hoursPassed, leak, currentWater);

                // Ввод объёма долива
                long V = InputReader.readLong("");

                // Добавление воды
                currentWater += V;
                events.add(new RefillEvent(T, V));
                previousTime = T;

                log.info("Добавлено {} л. Текущий уровень: {} л.", V, currentWater);
            }

            // Финальный результат
            long result = service.calculateWaterAfterLastRefill(events);
            log.info("Расчёт завершён. Итоговое количество воды: {} литров.", result);
            System.out.println(result);

        } catch (Exception ex) {
            GlobalExceptionHandler.handleUnexpectedError(ex);
        }
    }
}