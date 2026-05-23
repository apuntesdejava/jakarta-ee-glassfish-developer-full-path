package com.mycompany.projecttracker.adapter.in.metrics;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@ApplicationScoped
public class HttpMetricsRegistry {

    private final Map<HttpMetricKey, HttpMetricValue> metrics = new ConcurrentHashMap<>();

    public void record(String method, String path, int status, long durationNanos) {
        HttpMetricKey key = new HttpMetricKey(method, path, status);
        metrics.computeIfAbsent(key, ignored -> new HttpMetricValue()).record(durationNanos);
    }

    public String toPrometheus() {
        StringBuilder output = new StringBuilder();

        output.append("# HELP project_tracker_http_requests_total Total HTTP requests handled by ProjectTracker.\n");
        output.append("# TYPE project_tracker_http_requests_total counter\n");
        metrics.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> output
                .append("project_tracker_http_requests_total")
                .append(labels(entry.getKey()))
                .append(' ')
                .append(entry.getValue().count())
                .append('\n'));

        output.append("# HELP project_tracker_http_request_duration_seconds_sum Total request duration in seconds.\n");
        output.append("# TYPE project_tracker_http_request_duration_seconds_sum counter\n");
        metrics.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> output
                .append("project_tracker_http_request_duration_seconds_sum")
                .append(labels(entry.getKey()))
                .append(' ')
                .append(entry.getValue().durationSeconds())
                .append('\n'));

        output.append("# HELP project_tracker_http_request_duration_seconds_count Total measured HTTP requests.\n");
        output.append("# TYPE project_tracker_http_request_duration_seconds_count counter\n");
        metrics.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> output
                .append("project_tracker_http_request_duration_seconds_count")
                .append(labels(entry.getKey()))
                .append(' ')
                .append(entry.getValue().count())
                .append('\n'));

        return output.toString();
    }

    private String labels(HttpMetricKey key) {
        return "{method=\"" + escape(key.method()) + "\",path=\"" + escape(key.path()) + "\",status=\"" + key.status() + "\"}";
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private record HttpMetricKey(String method, String path, int status) implements Comparable<HttpMetricKey> {

        private static final Comparator<HttpMetricKey> COMPARATOR = Comparator
            .comparing(HttpMetricKey::path)
            .thenComparing(HttpMetricKey::method)
            .thenComparingInt(HttpMetricKey::status);

        @Override
        public int compareTo(HttpMetricKey other) {
            return COMPARATOR.compare(this, other);
        }
    }

    private static final class HttpMetricValue {

        private final LongAdder count = new LongAdder();
        private final LongAdder durationNanos = new LongAdder();

        void record(long elapsedNanos) {
            count.increment();
            durationNanos.add(Math.max(elapsedNanos, 0));
        }

        long count() {
            return count.sum();
        }

        double durationSeconds() {
            return durationNanos.sum() / 1_000_000_000.0;
        }
    }
}
