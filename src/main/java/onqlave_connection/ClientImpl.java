package onqlave_connection;

import com.google.gson.Gson;
import onqlave_contracts.BaseErrorResponse;
import onqlave_contracts.OnqlaveRequest;
import onqlave_errors.OnqlaveError;
import onqlave_logger.CustomLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import static onqlave_errors.ErrorCodes.SdkErrorCode;

public class ClientImpl implements Client {
    private final CustomLogger logger;
    private final HttpClient client;
    private final RetrySettings retrySettings;

    public ClientImpl(RetrySettings retrySettings, CustomLogger logger) {
        this.client = HttpClient.newHttpClient();
        this.retrySettings = retrySettings;
        this.logger = logger;
    }

    @Override
    public byte[] post(String resource, OnqlaveRequest body, Map<String, String> headers) throws IOException, InterruptedException, OnqlaveError {
        String operation = "Http";
//        logger.debug(String.format(HTTP_OPERATION_STARTED, operation));
        long start = System.currentTimeMillis();

        String jsonBody = new Gson().toJson(body);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(resource))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
        }

        HttpRequest request = requestBuilder.build();

        HttpResponse<InputStream> response = doRequestWithRetry(request);
        int statusCode = response.statusCode();

        if (statusCode == 429) {
            throw new OnqlaveError(SdkErrorCode, response.body().toString());
        } else if (statusCode >= 400) {
            BaseErrorResponse baseError = new Gson().fromJson(response.body().toString(), BaseErrorResponse.class);
            if (baseError != null) {
                throw new OnqlaveError(SdkErrorCode, baseError.getError().getMessage());
            } else {
                throw new OnqlaveError(SdkErrorCode, "Unknown error occurred");
            }
        }

        byte[] responseBody = response.body().readAllBytes();

//        logger.debug(String.format(onqlavemessages.HTTP_OPERATION_SUCCESS, operation, System.currentTimeMillis() - start));
        return responseBody;
    }

    private HttpResponse<InputStream> doRequestWithRetry(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<InputStream> response;
        int retries = 0;

        while (retries < retrySettings.getCount()) {
            response = client.send(request, BodyHandlers.ofInputStream());
            if (response.statusCode() < 500) {
                return response;
            }
            Thread.sleep(retrySettings.getMaxWaitTime().toMillis());
            retries++;
        }

        return client.send(request, BodyHandlers.ofInputStream());
    }
}