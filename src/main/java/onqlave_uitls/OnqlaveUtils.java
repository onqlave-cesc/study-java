package onqlave_uitls;

public class OnqlaveUtils {
    public static Hasher newHasher() {
        return new HasherImpl();
    }
}