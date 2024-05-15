package com.mercadolibre.urlshortener.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class UrlShortenerMetrics {

    private static final MeterRegistry registry = MetricRegistry.getRegistry();

    public static Timer timer(String topic) {
        return Timer.builder(topic)
                .register(registry);
    }

    public static Counter counter(String topic) {
        return Counter.builder(topic)
                .register(registry);
    }

    public static void incrementCounter(Counter counter) {
        counter.increment();
    }
}
