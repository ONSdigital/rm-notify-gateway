package uk.gov.ons.ctp.response.notify.lib.action;

public class InvalidSituationException extends RuntimeException {

  private static final String TOO_LONG_MESSAGE =
      "Situation can have a maximum length of %d; got \"%s\"";

  private InvalidSituationException(String message) {
    super(message);
  }

  public static InvalidSituationException tooLong(String situation) {
    return new InvalidSituationException(
        String.format(TOO_LONG_MESSAGE, Situation.MAXIMUM_LENGTH, situation));
  }
}
