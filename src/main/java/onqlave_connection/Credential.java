package onqlave_connection;

public class Credential {
  public final String accessKey;
  public final String signingKey;

  public Credential(String accessKey, String signingKey) {
      this.accessKey = accessKey;
      this.signingKey = signingKey;
  }
}
