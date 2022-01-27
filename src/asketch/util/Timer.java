package asketch.util;

public class Timer {

  private long startTime;
  private long stopTime;

  public Timer() {
    this.startTime = 0;
    this.stopTime = 0;
  }

  public void start() {
    startTime = System.nanoTime();
  }

  public void stop() {
    stopTime = System.nanoTime();
  }

  /**
   * Duration in milliseconds
   */
  private long duration() {
    return (stopTime - startTime) / 1000000;
  }

  public String getSeconds() {
    return duration() / 1000 + "s";
  }

  public String getMilliSeconds() {
    return duration() + "ms";
  }
}