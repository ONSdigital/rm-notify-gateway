package uk.gov.ons.ctp.response.notify.message;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;

/** The publisher of NotifyRequests to queues */
@MessageEndpoint
public class NotifyRequestPublisher {
  private static final Logger log = LoggerFactory.getLogger(NotifyRequestPublisher.class);

  @Qualifier("notifyRequestRabbitTemplate")
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Transactional(propagation = Propagation.REQUIRED)
  public void send(NotifyRequest notifyRequest) {
    log.with("notify_request", notifyRequest).debug("send to queue");
    rabbitTemplate.convertAndSend(notifyRequest);
  }
}
