package onqlave_contracts;

public class EncryptionOpenResponse extends BaseErrorResponse {
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