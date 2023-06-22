package onqlave_logger;

import org.slf4j.Logger;

class WithDevelopment implements Option {
    private final boolean isDevelop;

    public WithDevelopment(boolean isDevelop) {
        this.isDevelop = isDevelop;
    }

    @Override
    public void apply(Logger logger) {
//        logger.setIsDevelopment(isDevelop);
    }
}
