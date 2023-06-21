package onqlave_contracts;
public class EncryptionSecurityModel {
    private String algorithm;
    private String wrappingAlgorithm;

    public EncryptionSecurityModel(String algorithm, String wrappingAlgorithm) {
        this.algorithm = algorithm;
        this.wrappingAlgorithm = wrappingAlgorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getWrappingAlgorithm() {
        return wrappingAlgorithm;
    }

    public void setWrappingAlgorithm(String wrappingAlgorithm) {
        this.wrappingAlgorithm = wrappingAlgorithm;
    }
}

