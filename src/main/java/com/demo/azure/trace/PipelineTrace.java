package com.demo.azure.trace;

import org.slf4j.MDC;

/** Full-chain trace id in logs ({@link org.slf4j.MDC}). */
public final class PipelineTrace {

    public static final String MDC_KEY = "traceId";

    private PipelineTrace() {}

    public static void put(String traceId) {
        if (traceId != null && !traceId.isBlank()) {
            MDC.put(MDC_KEY, traceId);
        }
    }

    public static void clear() {
        MDC.remove(MDC_KEY);
    }
}
