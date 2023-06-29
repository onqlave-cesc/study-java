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
import onqlave_key_manager.factories.RSASSAPKCS1SHAKeyFactory;
import onqlave_key_manager.operations.rsaSSAPKCS1SHA2562048KeyOperation;
import onqlave_key_manager.services.*;
import onqlave_key_manager.types.OperationName;
import onqlave_key_manager.types.Unwrapping;
import onqlave_key_manager.types.WrappingKeyFactory;
import onqlave_key_manager.types.WrappingKeyOperation;

import static onqlave_errors.ErrorCodes.SdkErrorCode;

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
  private Map<String, WrappingKeyOperation> operations;

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

    OnqlaveConnection httpClient = new OnqlaveConnection(onqConfig, hasher, onqlaveLogger);
    WrappingKeyFactory rsaSSAPKCS1KeyFactory = new RSASSAPKCS1SHAKeyFactory(randomService);
    Map<String, WrappingKeyOperation> operations = new HashMap<String, WrappingKeyOperation>();
    operations.put(OperationName.RsaSsapkcs12048sha256f4, new rsaSSAPKCS1SHA2562048KeyOperation(rsaSSAPKCS1KeyFactory));

    this.keyMng = httpClient;
    this.config = config;
    this.logger = onqlaveLogger;
    this.operations = operations;
  }

  public Triplet<byte[], byte[], String> fetchEncryptionKey() throws Exception {
    String operation = "FetchEncryptionKey";
    EncryptionOpenRequest request = new EncryptionOpenRequest();
    byte[] data;
    Triplet<byte[], byte[], String> trip;
    try {
      data = this.keyMng.post(ENCRYPT_RESOURCE_URL, request);
      EncryptionOpenResponse response = new Gson().fromJson(data.toString(), EncryptionOpenResponse.class);
      byte[] edk = response.getDK().getEncryptedDataKey().getBytes();
      byte[] wdk = response.getDK().getWrappedDataKey().getBytes();
      byte[] epk = response.getWK().getEncryptedPrivateKey().getBytes();
      byte[] fp = response.getWK().getKeyFingerprint().getBytes();
      String wrappingAlgo = response.getSecurityModel().getWrappingAlgorithm();
      String algo = response.getSecurityModel().getAlgorithm();
      byte[] dk = this.unwrapKey(wrappingAlgo, operation, wdk, epk, fp, this.config.credential.getSecretKey().getBytes());
      trip = Triplet.with(edk, dk, algo);
    } catch (Exception e) {
      throw new OnqlaveError(ErrorCodes.SdkErrorCode, e.getMessage(), null);
    }

    return trip;
  }

  public byte[] fetchDecryptionKey(byte[] edk) throws Exception {
    String operation = "FetchDecryptionKey";
    DecryptionOpenRequest request = new DecryptionOpenRequest(edk.toString());
    byte[] dk;
    try {
      byte[] data = this.keyMng.post(DECRYPT_RESOURCE_URL, request);
      DecryptionOpenResponse response = new Gson().fromJson(data.toString(), DecryptionOpenResponse.class);
      byte[] wdk = response.getDK().getWrappedDataKey().getBytes();
      byte[] epk = response.getWK().getEncryptedPrivateKey().getBytes();
      byte[] fp = response.getWK().getKeyFingerprint().getBytes();
      String wrappingAlgo = response.getSecurityModel().getWrappingAlgorithm();
      dk = this.unwrapKey(wrappingAlgo, operation, wdk, epk, fp, this.config.credential.getSecretKey().getBytes());
    } catch (Exception e) {
      throw new OnqlaveError(ErrorCodes.SdkErrorCode, e.getMessage(), null);
    }

    return dk;
  }

  private byte[] unwrapKey(String algo, String operation, byte[] wdk, byte[] epk, byte[] fp, byte[] password) throws Exception {
    if (!this.operations.containsKey(OperationName.RsaSsapkcs12048sha256f4)) {
      throw new OnqlaveError(SdkErrorCode, Messages.KEY_INVALID_WRAPPING_ALGO, null);
    }
    WrappingKeyOperation wrappingOp = this.operations.get(OperationName.RsaSsapkcs12048sha256f4);
    WrappingKeyFactory factory = wrappingOp.getFactory();
    byte[] dk;
    try {
      Unwrapping primitive = factory.primitive(wrappingOp);
      dk = primitive.unwrapKey(wdk, epk, fp, password);
    } catch (Exception e) {
      throw e;
    }

    return dk;
  }
}
