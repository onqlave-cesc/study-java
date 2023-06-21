package onqlave_connection;

import java.time.Duration;

public class RetrySettings {
    private int count;
    private Duration waitTime;
    private Duration maxWaitTime;

    public RetrySettings(int count, Duration waitTime, Duration maxWaitTime) {
        this.count = count;
        this.waitTime = waitTime;
        this.maxWaitTime = maxWaitTime;
    }

    public int getCount() {
        return count;
    }

    public Duration getWaitTime() {
        return waitTime;
    }

    public Duration getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setWaitTime(Duration waitTime) {
        this.waitTime = waitTime;
    }

    public void setMaxWaitTime(Duration maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public void valid() throws IllegalArgumentException {
        if (count <= 0) {
            throw new IllegalArgumentException("Invalid retry count");
        }
        if (waitTime.toMillis() <= 0) {
            throw new IllegalArgumentException("Invalid wait time");
        }
    }
}
