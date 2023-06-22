package onqlave_contracts;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.IOException;

class DecryptionOpenRequest implements OnqlaveRequest {
    @NotNull
    @Max(1500)
    private String encryptedDataKey;

    public DecryptionOpenRequest(String encryptedDataKey) {
        this.encryptedDataKey = encryptedDataKey;
    }

    public String getEncryptedDataKey() {
        return encryptedDataKey;
    }

    public void setEncryptedDataKey(String encryptedDataKey) {
        this.encryptedDataKey = encryptedDataKey;
    }

    @Override
    public byte[] getContent() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(this);
    }
}
