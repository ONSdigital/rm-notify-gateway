package uk.gov.ons.ctp.response.notify.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.notify.domain.Response;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.domain.repository.MessageRepository;
import uk.gov.ons.ctp.response.notify.lib.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.message.NotifyRequestPublisher;

@Service
public class ResilienceService {
  private static final Logger log = LoggerFactory.getLogger(ResilienceService.class);

  @Autowired private MessageRepository messageRepository;

  @Autowired private NotifyRequestPublisher notifyRequestPublisher;

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public Response process(NotifyRequest notifyRequest) {
    String templateId = notifyRequest.getTemplateId();
    log.with("template_id", templateId).debug("Entering process");

    UUID theId = UUID.randomUUID();
    messageRepository.save(Message.builder().id(theId).build());
    log.with("id", theId).debug("Message persisted");

    notifyRequest.setId(theId.toString());
    notifyRequestPublisher.send(notifyRequest);
    log.with("notify_request", notifyRequest).debug("Now on queue");

    return Response.builder()
        .id(theId)
        .reference(notifyRequest.getReference())
        .templateId(getTemplateUUID(templateId))
        .fromNumber(notifyRequest.getPhoneNumber())
        .fromEmail(notifyRequest.getEmailAddress())
        .build();
  }

  private UUID getTemplateUUID(String templateId) {
    try {
      return UUID.fromString(templateId);
    } catch (IllegalArgumentException e) {
      log.warn("Template id is not a UUID " + templateId, e);
      return null;
    }
  }

  public void update(Message message) {
    log.with("message", message).debug("Entering update");
    Message existingMessage = messageRepository.findById(message.getId());
    log.debug("existingMessage is {}", existingMessage);
    if (existingMessage != null) {
      message.setMessagePK(existingMessage.getMessagePK());
      messageRepository.saveAndFlush(message);
    } else {
      // We should never come here as prior to the request being sent to GOV.UK Notify, a message
      // was stored in
      // the DB.
      log.error("No existing message found to update with notificationID from GOV.UK Notify");
    }
  }

  public Message findMessageById(UUID id) {
    log.with("id", id).debug("Entering findMessageById");
    return messageRepository.findById(id);
  }
}
