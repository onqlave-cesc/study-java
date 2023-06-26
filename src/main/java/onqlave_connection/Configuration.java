package onqlave_connection;

public class Configuration {
  public final Credential credential;
    public final RetrySettings retry;
    public final String arxURL;
    public final String arxID;

    public Configuration(Credential credential, RetrySettings retry, String arxURL, String arxID) {
        this.credential = credential;
        this.retry = retry;
        this.arxURL = arxURL;
        this.arxID = arxID;
    }
}
