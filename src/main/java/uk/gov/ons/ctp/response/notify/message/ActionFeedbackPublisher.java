package uk.gov.ons.ctp.response.notify.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;

/** The service that publishes ActionFeedbacks to queue. */
@MessageEndpoint
@Slf4j
public class ActionFeedbackPublisher {

  @Qualifier("actionFeedbackRabbitTemplate")
  @Autowired
  private RabbitTemplate rabbitTemplate;

  /**
   * To put an ActionFeedback on the queue
   *
   * @param actionFeedback the ActionFeedback to put on the queue
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void sendFeedback(ActionFeedback actionFeedback) {
    log.debug("Entering sendFeedback for actionId {}", actionFeedback.getActionId());
    rabbitTemplate.convertAndSend(actionFeedback);
  }
}
