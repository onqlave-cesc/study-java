package onqlave_logger;

public interface Filter {
    public Object doFilter(Object any);
    public boolean isFilter(String str, Object any);
}