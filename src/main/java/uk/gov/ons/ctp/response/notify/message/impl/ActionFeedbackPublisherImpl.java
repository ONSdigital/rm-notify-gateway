package uk.gov.ons.ctp.response.notify.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Publisher;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.notify.message.ActionFeedbackPublisher;

import javax.inject.Named;

/**
 * The service that puts ActionFeedbacks on the outbound channel actionFeedbackOutbound
 */
@Named
@Slf4j
public class ActionFeedbackPublisherImpl implements ActionFeedbackPublisher {
  /**
   * To put an ActionFeedback on the outbound channel actionFeedbackOutbound
   * @param actionFeedback the ActionFeedback to put on the outbound channel actionFeedbackOutbound
   * @return an ActionFeedback
   */
  @Publisher(channel = "actionFeedbackOutbound")
  public ActionFeedback sendFeedback(ActionFeedback actionFeedback) {
    log.debug("Entering sendFeedback for actionId {}", actionFeedback.getActionId());
    return actionFeedback;
  }
}
