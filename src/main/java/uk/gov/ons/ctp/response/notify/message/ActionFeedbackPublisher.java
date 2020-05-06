package uk.gov.ons.ctp.response.notify.message;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;

/** The service that publishes ActionFeedbacks to queue. */
@MessageEndpoint
public class ActionFeedbackPublisher {
  private static final Logger log = LoggerFactory.getLogger(ActionFeedbackPublisher.class);

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
    log.with("action_id", actionFeedback.getActionId()).debug("Entering sendFeedback");
    rabbitTemplate.convertAndSend(actionFeedback);
  }
}
