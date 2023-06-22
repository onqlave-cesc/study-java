package onqlave_contracts;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;

public class EncryptionOpenRequest implements OnqlaveRequest, Serializable {
    @Override
    public byte[] getContent() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes("this");
    }


}
