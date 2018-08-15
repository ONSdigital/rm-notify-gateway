package uk.gov.ons.ctp.response.notify.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;

/** The publisher of NotifyRequests to queues */
@Slf4j
@MessageEndpoint
public class NotifyRequestPublisher {

  @Qualifier("notifyRequestRabbitTemplate")
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Transactional(propagation = Propagation.REQUIRED)
  public void send(NotifyRequest notifyRequest) {
    log.debug("send to queue notifyRequest {}", notifyRequest);
    rabbitTemplate.convertAndSend(notifyRequest);
  }
}
