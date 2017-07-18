package uk.gov.ons.ctp.response.notify.message.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.notify.message.ActionFeedbackPublisher;

/**
 * The service that publishes ActionFeedbacks to queue.
 */
@MessageEndpoint
@Slf4j
public class ActionFeedbackPublisherImpl implements ActionFeedbackPublisher {

  @Qualifier("actionFeedbackRabbitTemplate")
  @Autowired
  private RabbitTemplate rabbitTemplate;

  /**
   * To put an ActionFeedback on the queue
   * @param actionFeedback the ActionFeedback to put on the queue
   */
  public void sendFeedback(ActionFeedback actionFeedback) {
    log.debug("Entering sendFeedback for actionId {}", actionFeedback.getActionId());
    rabbitTemplate.convertAndSend(actionFeedback);
  }
}