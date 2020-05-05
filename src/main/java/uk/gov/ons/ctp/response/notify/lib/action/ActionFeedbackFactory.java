package uk.gov.ons.ctp.response.notify.lib.action;

import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;

public class ActionFeedbackFactory {
  public static ActionFeedback create(String actionId, Situation situation, Outcome outcome) {
    return new ActionFeedback(actionId, situation.toString(), outcome);
  }
}
