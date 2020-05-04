package uk.gov.ons.ctp.response.notify.message;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.lib.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.ons.ctp.response.notify.service.ResilienceService;
import uk.gov.service.notify.NotificationClientException;

/** The service that reads NotifyRequests from the inbound channel */
@MessageEndpoint
public class NotifyRequestReceiver {
  private static final Logger log = LoggerFactory.getLogger(NotifyRequestReceiver.class);

  @Autowired private NotifyService notifyService;

  @Autowired private ResilienceService resilienceService;

  @Autowired private NotifyConfiguration notifyConfiguration;

  /**
   * To process NotifyRequests from the input channel notifyRequestTransformed
   *
   * @param notifyRequest the NotifyRequest to be processed
   * @throws NotificationClientException when GOV.UK Notify does
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  @ServiceActivator(
      inputChannel = "notifyRequestTransformed",
      adviceChain = "notifyRequestRetryAdvice")
  public void process(final NotifyRequest notifyRequest) throws NotificationClientException {
    log.with("notify_request", notifyRequest).debug("entering process");
    if (notifyConfiguration.getEnabled()) {
      try {
        UUID notificationId = notifyService.process(notifyRequest);
        resilienceService.update(
            Message.builder()
                .id(UUID.fromString(notifyRequest.getId()))
                .notificationId(notificationId)
                .build());
      } catch (NotificationClientException nce) {
        log.with("id", notifyRequest.getId())
            .with("template_id", notifyRequest.getTemplateId())
            .with("status_code", nce.getHttpResult())
            .error("Error sending request to Gov.Notify", nce);
        // re-throw to maintain current functionality
        throw nce;
      }
    } else {
      log.with("notify_request", notifyRequest)
          .info("Not put on processing message as Gov Notify integration is disabled");
    }
  }
}
