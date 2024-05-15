package com.mercadolibre.urlshortener.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.Getter;

public class MetricRegistry {
    @Getter
    private static final MeterRegistry registry = new SimpleMeterRegistry();

}
