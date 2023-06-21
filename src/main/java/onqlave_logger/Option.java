package onqlave_logger;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;

public interface Option {
    void apply(Logger logger);
}

class WithFilters implements Option {
    private final Filter[] filters;

    public WithFilters(Filter... filters) {
        this.filters = filters;
    }

    @Override
    public void apply(Logger logger) {
        logger.addFilters(filters);
    }
}

class WithCid implements Option {
    private final String cid;

    public WithCid(String cid) {
        this.cid = cid;
    }

    @Override
    public void apply(Logger logger) {
        String correlationID = cid;
        if (correlationID == null || correlationID.isEmpty()) {
            correlationID = UUID.randomUUID().toString();
        }

        logger.setCorrelationID(correlationID);
    }
}

class WithTimeEncoder implements Option {
    private final TimeEncoder encoder;

    public WithTimeEncoder(TimeEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void apply(Logger logger) {
        logger.setTimeEncoder(encoder);
    }
}

class WithDurationEncoder implements Option {
    private final DurationEncoder encoder;

    public WithDurationEncoder(DurationEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void apply(Logger logger) {
        logger.setDurationEncoder(encoder);
    }
}

class WithDevelopment implements Option {
    private final boolean isDevelop;

    public WithDevelopment(boolean isDevelop) {
        this.isDevelop = isDevelop;
    }

    @Override
    public void apply(Logger logger) {
        logger.setIsDevelopment(isDevelop);
    }
}

class WithLevel implements Option {
    private final Level level;

    public WithLevel(Level level) {
        this.level = level;
    }

    @Override
    public void apply(Logger logger) {
        logger.setLevel(level);
    }
}