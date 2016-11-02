package uk.gov.ons.ctp.response.notify.message;

import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;

/**
 * The service that puts ActionFeedbacks on the outbound channel actionFeedbackOutbound
 */
public interface ActionFeedbackPublisher {
  /**
   * To put ActionFeedback on the outbound channel
   * @param actionFeedback the ActionFeedback to put on the outbound channel actionFeedbackOutbound
   * @return ActionFeedback
   */
  ActionFeedback sendFeedback(ActionFeedback actionFeedback);
}
