package onqlave_logger;

import org.slf4j.Logger;

import java.util.UUID;

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

//        logger.setCorrelationID(correlationID);
    }
}
