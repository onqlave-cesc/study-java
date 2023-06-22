package onqlave_logger;

import org.slf4j.Logger;

class WithFilters implements Option {
    private final Filter[] filters;

    public WithFilters(Filter... filters) {
        this.filters = filters;
    }

    @Override
    public void apply(Logger logger) {
//        logger.addFilters(filters);
    }
}
