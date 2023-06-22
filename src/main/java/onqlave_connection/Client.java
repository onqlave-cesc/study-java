package onqlave_connection;

import onqlave_contracts.OnqlaveRequest;
import onqlave_errors.OnqlaveError;

import java.io.IOException;
import java.util.Map;

public interface Client {
    byte[] post(String resource, OnqlaveRequest body, Map<String, String> headers) throws IOException, InterruptedException, OnqlaveError;
}

