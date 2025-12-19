package com.humidifier.domain;

public record RefillEvent(long time, long volume) {
    // Метод для расчёта утекшей воды между событиями
    public static long calculateLeak(long currentTime, long previousTime, long previousWater) {
        long timePassed = currentTime - previousTime;
        return Math.min(previousWater, timePassed); // Утекает максимум сколько есть
    }
}