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

            for (int i = 1; i <= N; i++) {
                // Ввод времени
                long T = InputReader.readLong("Введите T_" + i + ": ");

                // Рассчёт утечки перед доливом (кроме первого события)
                if (i > 1) {
                    long leak = RefillEvent.calculateLeak(T, previousTime, currentWater);
                    long hoursPassed = T - previousTime;
                    log.info("Прошло {} часа(ов), утекло {} литра(ов) воды",
                            hoursPassed, leak);
                    currentWater = Math.max(0, currentWater - leak);
                }

                // Ввод объёма долива
                long V = InputReader.readLong("Введите V_" + i + ": ");

                // Добавление воды
                currentWater += V;
                log.debug("Добавлено {} л. Текущий уровень: {} л.", V, currentWater);

                // Сохраняем событие
                events.add(new RefillEvent(T, V));
                previousTime = T;
            }

            // Финальный результат
            long result = service.calculateWaterAfterLastRefill(events);
            log.info("Расчёт завершён. Итоговое количество воды: {} литров.", result);
            System.out.println(result);

        } catch (AppException ex) {
            GlobalExceptionHandler.handleAppException(ex);
        } catch (Exception ex) {
            GlobalExceptionHandler.handleUnexpectedError(ex);
        }
    }
}