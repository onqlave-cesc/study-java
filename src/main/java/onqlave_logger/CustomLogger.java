package onqlave_logger;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class CustomLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomLogger.class);

    private final Tracer tracer;
    private final String serviceName;
    private final List<Filter> filters;
    private final boolean isDevelopment;

    public CustomLogger(String serviceName, boolean isDevelopment, List<Filter> filters) {
        this.tracer = GlobalTracer.get();
        this.serviceName = serviceName;
        this.filters = filters;
        this.isDevelopment = isDevelopment;
    }

    public void logInfo(String message, Map<String, Object> fields) {
        log(LogLevel.INFO, message, fields);
    }

    public void logError(String message, Throwable throwable, Map<String, Object> fields) {
        log(LogLevel.ERROR, message, fields, throwable);
    }

    private void log(LogLevel level, String message, Map<String, Object> fields) {
        log(level, message, fields, null);
    }

    private void log(LogLevel level, String message, Map<String, Object> fields, Throwable throwable) {
        Span span = tracer.buildSpan(message).start();
        try {
            span.setTag(Tags.COMPONENT.getKey(), serviceName);
            span.setTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER);

            if (fields != null) {
                for (Map.Entry<String, Object> entry : fields.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    for (Filter filter : filters) {
                        if (filter.isFilter(key, value)) {
                            value = filter.doFilter(value);
                        }
                    }
                    span.setTag(key, (String) value);
                }
            }

            if (throwable != null) {
                span.log(Map.of(
                        "event", Tags.ERROR.getKey(),
                        "error.object", throwable,
                        "stack", throwable.getStackTrace()
                ));
            }

            LOGGER.info(message);
        } catch (Exception e) {
            LOGGER.error("Error logging span", e);
        } finally {
            span.finish();
        }
    }

    private enum LogLevel {
        INFO,
        ERROR
    }


}