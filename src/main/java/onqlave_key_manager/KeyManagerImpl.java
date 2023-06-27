package onqlave_key_manager;

import java.util.*;

import org.javatuples.Triplet;

import com.google.gson.Gson;

import onqlave_connection.*;
import onqlave_contracts.*;
import onqlave_errors.ErrorCodes;
import onqlave_errors.OnqlaveError;
import onqlave_logger.*;
import onqlave_messages.Messages;
import onqlave_uitls.Hasher;
import onqlave_uitls.HasherImpl;
import onqlave_key_manager.services.*;

class Configuration {
  public onqlave_credentials.Credential credential;
  public RetrySettings retry;
  public String arxUrl;
  public Boolean debug;

  public Configuration(onqlave_credentials.Credential credential, RetrySettings retry, String arxURL, Boolean debug) {
    this.credential = credential;
    this.retry = retry;
    this.arxUrl = arxURL;
    this.debug = debug;
  }
}

public class KeyManagerImpl implements KeyManager {
  static final String ENCRYPT_RESOURCE_URL = "oe2/keymanager/encrypt";
  static final String DECRYPT_RESOURCE_URL = "oe2/keymanager/decrypt";

  private Connection keyMng;
  private Configuration config;
  private CustomLogger logger;
  private Map<String, String> operations;

  public KeyManagerImpl(Configuration config, CPRNGService randomService) {
    Hasher hasher = new HasherImpl();
    CustomLogger onqlaveLogger = new CustomLogger(Messages.SDK, false, null);
    int index = config.arxUrl.lastIndexOf("/");
    onqlave_connection.Credential cred = new onqlave_connection.Credential(ENCRYPT_RESOURCE_URL, DECRYPT_RESOURCE_URL);
    onqlave_connection.Configuration onqConfig = new onqlave_connection.Configuration(
      cred,
      config.retry,
      config.arxUrl.substring(0, index),
      config.arxUrl.substring(index+1)
    );

    OnqlaveConnection httpClient = new OnqlaveConnection(onqConfig, null, onqlaveLogger);

    this.keyMng = httpClient;
    this.config = config;
    this.logger = onqlaveLogger;
  }

  public Triplet<byte[], byte[], String> fetchEncryptionKey() throws Exception {
    String operation = "FetchEncryptionKey";
    EncryptionOpenRequest request = new EncryptionOpenRequest();
    byte[] data;
    try {
      data = this.keyMng.post(ENCRYPT_RESOURCE_URL, request);
    } catch (Exception e) {
      throw new OnqlaveError(ErrorCodes.SdkErrorCode, e.getMessage(), null);
    }
    EncryptionOpenResponse response = new Gson().fromJson(data.toString(), EncryptionOpenResponse.class);
    byte[] edk = response.getDK().getEncryptedDataKey().getBytes();
    byte[] wdk = response.getDK().getWrappedDataKey().getBytes();
    byte[] epk = response.getWK().getEncryptedPrivateKey().getBytes();
    byte[] fp = response.getWK().getKeyFingerprint().getBytes();
    String wrappingAlgo = response.getSecurityModel().getWrappingAlgorithm();
    String algo = response.getSecurityModel().getAlgorithm();
    byte[] dk = this.unwrapKey(wrappingAlgo, operation, wdk, epk, fp, this.config.credential.getSecretKey().getBytes());
    Triplet<byte[], byte[], String> trip = Triplet.with(edk, dk, algo);
    return trip;
  }

  public byte[] fetchDecryptionKey(byte[] edk) throws Exception {
    String operation = "FetchDecryptionKey";
    DecryptionOpenRequest request = new DecryptionOpenRequest(edk.toString());
    byte[] data;
    try {
      data = this.keyMng.post(DECRYPT_RESOURCE_URL, request);
    } catch (Exception e) {
      throw new OnqlaveError(ErrorCodes.SdkErrorCode, e.getMessage(), null);
    }
    DecryptionOpenResponse response = new Gson().fromJson(data.toString(), DecryptionOpenResponse.class);
    byte[] wdk = response.getDK().getWrappedDataKey().getBytes();
    byte[] epk = response.getWK().getEncryptedPrivateKey().getBytes();
    byte[] fp = response.getWK().getKeyFingerprint().getBytes();
    String wrappingAlgo = response.getSecurityModel().getWrappingAlgorithm();
    byte[] dk = this.unwrapKey(wrappingAlgo, operation, wdk, epk, fp, this.config.credential.getSecretKey().getBytes());
    return dk;
  }

  private byte[] unwrapKey(String algo, String operation, byte[] wdk, byte[] epk, byte[] fp, byte[] password) throws Exception {
    byte[] dk = {};
    return dk;
  }
}
