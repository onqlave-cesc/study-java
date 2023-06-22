package onqlave_uitls;

import onqlave_contracts.OnqlaveRequest;

import java.util.Map;

public interface Hasher {
    String digest(OnqlaveRequest body) throws Exception;

    String sign(Map<String, String> headers, String signingKey) throws Exception;
}
