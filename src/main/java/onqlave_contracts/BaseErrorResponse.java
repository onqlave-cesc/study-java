package onqlave_contracts;

public class BaseErrorResponse {
    private Error error;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

class Error {
    private String status;
    private String message;
    private String correlationID;
    private Object[] details;
    private int code;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public Object[] getDetails() {
        return details;
    }

    public void setDetails(Object[] details) {
        this.details = details;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

class DecryptionOpenResponse extends BaseErrorResponse {
    private WrappingKey WK;
    private EncryptionSecurityModel securityModel;
    private DataDecryptionKey DK;

    public WrappingKey getWK() {
        return WK;
    }

    public void setWK(WrappingKey WK) {
        this.WK = WK;
    }

    public EncryptionSecurityModel getSecurityModel() {
        return securityModel;
    }

    public void setSecurityModel(EncryptionSecurityModel securityModel) {
        this.securityModel = securityModel;
    }

    public DataDecryptionKey getDK() {
        return DK;
    }

    public void setDK(DataDecryptionKey DK) {
        this.DK = DK;
    }
}

class EncryptionOpenResponse extends BaseErrorResponse {
    private WrappingKey WK;
    private DataEncryptionKey DK;
    private EncryptionSecurityModel securityModel;
    private int maxUses;

    public WrappingKey getWK() {
        return WK;
    }

    public void setWK(WrappingKey WK) {
        this.WK = WK;
    }

    public DataEncryptionKey getDK() {
        return DK;
    }

    public void setDK(DataEncryptionKey DK) {
        this.DK = DK;
    }

    public EncryptionSecurityModel getSecurityModel() {
        return securityModel;
    }

    public void setSecurityModel(EncryptionSecurityModel securityModel) {
        this.securityModel = securityModel;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }
}