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

    public void start() {
        try {
            log.info("Запуск консольного интерфейса.");

            int N = InputReader.readInt("Введите количество доливов N: ");

            List<RefillEvent> events = new ArrayList<>();

            for (int i = 1; i <= N; i++) {
                long T = InputReader.readLong("Введите T_" + i + ": ");
                long V = InputReader.readLong("Введите V_" + i + ": ");

                events.add(new RefillEvent(T, V));
            }

            long result = service.calculateWaterAfterLastRefill(events);

            log.info("Расчёт завершён успешно. Итоговое количество воды: {} литров.", result);
            System.out.println(result);

        } catch (AppException ex) {
            GlobalExceptionHandler.handleAppException(ex);
        } catch (Exception ex) {
            GlobalExceptionHandler.handleUnexpectedError(ex);
        }
    }
}
