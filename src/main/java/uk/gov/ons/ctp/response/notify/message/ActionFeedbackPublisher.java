package uk.gov.ons.ctp.response.notify.message;

import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;

/** The service that publishes ActionFeedbacks to queue. */
public interface ActionFeedbackPublisher {
  /**
   * To send an ActionFeedback to queue.
   *
   * @param actionFeedback the ActionFeedback to put on the queue
   */
  void sendFeedback(ActionFeedback actionFeedback);
}
