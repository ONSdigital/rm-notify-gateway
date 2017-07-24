package uk.gov.ons.ctp.response.notify.message.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.message.NotifyRequestPublisher;

/**
 * The publisher of NotifyRequests to queues
 */
@Slf4j
@MessageEndpoint
public class NotifyRequestPublisherImpl implements NotifyRequestPublisher {

  @Qualifier("notifyRequestRabbitTemplate")
  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Override
  public void send(NotifyRequest notifyRequest) {
    log.debug("send to queue notifyRequest {}", notifyRequest);
    rabbitTemplate.convertAndSend(notifyRequest);
  }
}
