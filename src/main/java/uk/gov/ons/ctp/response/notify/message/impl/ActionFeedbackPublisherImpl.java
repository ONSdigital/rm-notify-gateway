package uk.gov.ons.ctp.response.notify.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.notify.message.ActionFeedbackPublisher;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * The service that puts ActionFeedbacks on the outbound channel actionFeedbackOutbound
 */
@Named
@Slf4j
public class ActionFeedbackPublisherImpl implements ActionFeedbackPublisher {

//  @Qualifier("actionFeedbackRabbitTemplate")
//  @Inject
//  private RabbitTemplate rabbitTemplate;

  /**
   * To put an ActionFeedback on the outbound channel actionFeedbackOutbound
   * @param actionFeedback the ActionFeedback to put on the outbound channel actionFeedbackOutbound
   */
  public void sendFeedback(ActionFeedback actionFeedback) {
    log.debug("Entering sendFeedback for actionId {}", actionFeedback.getActionId());
    // TODO Leave this binding hardcoded? The same value is used in the xml flow.
//    rabbitTemplate.convertAndSend("Action.Feedback.binding", actionFeedback);
  }
}
