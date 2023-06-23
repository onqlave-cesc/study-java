package onqlave_key_manager.services;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;

public interface CPRNGService {
    byte[] getRandomBytes(int size) throws Exception;
    int getRandomInt() throws Exception;
    InputStream getRandomInputStream() throws Exception;
}

class CPRNGServiceImpl implements CPRNGService {
    private final SecureRandom secureRandom;

    CPRNGServiceImpl(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public byte[] getRandomBytes(int size) {
        byte[] bytes = new byte[size];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public int getRandomInt() {
        byte[] bytes = new byte[4];
        secureRandom.nextBytes(bytes);
        return (bytes[0] & 0xFF) << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    public InputStream getRandomInputStream() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                byte[] b = new byte[1];
                secureRandom.nextBytes(b);
                return b[0];
            }
        };
    }
}
