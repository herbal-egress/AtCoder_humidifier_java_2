package com.humidifier.service;

import com.humidifier.domain.RefillEvent;
import com.humidifier.exception.AppException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class HumidifierService {

    public long calculateWaterAfterLastRefill(List<RefillEvent> events) {

        if (events.isEmpty()) {
            throw new AppException("Список событий долива пуст. Невозможно выполнить расчёт.");
        }

        long water = 0;
        long prevTime = 0;

        for (RefillEvent event : events) {

            log.debug("Обработка события: время={}, добавлено={} л.",
                    event.time(), event.volume());

            long delta = event.time() - prevTime;

            // уменьшение воды
            water = Math.max(0, water - delta);

            // долив
            water += event.volume();

            log.debug("Состояние после события: {} л.", water);

            prevTime = event.time();
        }

        log.info("Итоговое количество воды после последнего долива: {} л.", water);
        return water;
    }
}
