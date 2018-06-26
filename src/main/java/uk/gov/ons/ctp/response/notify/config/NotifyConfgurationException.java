package uk.gov.ons.ctp.response.notify.config;

public class NotifyConfgurationException extends RuntimeException {
  public NotifyConfgurationException() {}

  public NotifyConfgurationException(String message) {
    super(message);
  }

  public NotifyConfgurationException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotifyConfgurationException(Throwable cause) {
    super(cause);
  }

  public NotifyConfgurationException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
