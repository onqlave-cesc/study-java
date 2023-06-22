package onqlave_contracts;

import java.io.IOException;

public interface OnqlaveRequest {
    byte[] getContent() throws IOException;
}

