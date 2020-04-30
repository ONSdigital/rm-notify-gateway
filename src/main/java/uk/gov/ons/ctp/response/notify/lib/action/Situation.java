package uk.gov.ons.ctp.response.notify.lib.action;

public class Situation {
  public static final int MAXIMUM_LENGTH = 100;
  private final String situation;

  public Situation(String situation) {
    if (situation.length() > MAXIMUM_LENGTH) {
      throw InvalidSituationException.tooLong(situation);
    }

    this.situation = situation;
  }

  @Override
  public String toString() {
    return situation;
  }
}
