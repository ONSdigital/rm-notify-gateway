package uk.gov.ons.ctp.response.notify.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.notify.domain.SendSmsResponse;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.domain.repository.MessageRepository;
import uk.gov.ons.ctp.response.notify.message.NotifyRequestPublisher;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.ons.ctp.response.notify.service.ResilienceService;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientException;

import java.util.UUID;

@Slf4j
@Service
public class ResilienceServiceImpl implements ResilienceService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private NotifyRequestPublisher notifyRequestPublisher;

    @Autowired
    private NotifyService notifyService;

    private static final String GOV_UK_NOTIFY_ISSUE =
            "An error occurred retrieving Notification from GOV.UK Notify. Message is %s. Cause is %s.";

    @Transactional(readOnly = false)
    @Override
    public SendSmsResponse process(NotifyRequest notifyRequest) {
        String templateId = notifyRequest.getTemplateId();
        String phoneNumber = notifyRequest.getPhoneNumber();
        log.debug("Entering process with templateId {} - phoneNumber {}", templateId, phoneNumber);

        UUID theId = UUID.randomUUID();
        messageRepository.save(Message.builder().id(theId).build());
        log.debug("Message persisted with id {}", theId);

        notifyRequest.setId(theId.toString());
        notifyRequestPublisher.send(notifyRequest);
        log.debug("notifyRequest {} now on queue", notifyRequest);

        return SendSmsResponse.builder()
                .id(theId)
                .reference(notifyRequest.getReference())
                .templateId(UUID.fromString(templateId))
                .fromNumber(phoneNumber)
                .build();
    }

    @Override
    public void update(Message message) {
        log.debug("Entering update with message {}", message);
        Message existingMessage = messageRepository.findById(message.getId());
        log.debug("existingMessage is {}", existingMessage);
        if (existingMessage != null) {
            message.setMessagePK(existingMessage.getMessagePK());
            messageRepository.saveAndFlush(message);
        } else {
            // We should never come here as prior to the request being sent to GOV.UK Notify, a message was stored in
            // the DB.
            log.error("No existing message found to update with notificationID from GOV.UK Notify");
        }
    }

    @Override
    public Notification findNotificationByMessageId(UUID messageId) throws CTPException {
        log.debug("Entering findNotificationByMessageId with messageId {}", messageId);
        Notification result = null;

        Message existingMessage = messageRepository.findById(messageId);
        if (existingMessage != null) {
            try {
                result = notifyService.findNotificationById(existingMessage.getNotificationId());
            } catch(NotificationClientException e) {
                String errorMsg = String.format(GOV_UK_NOTIFY_ISSUE, e.getMessage(), e.getCause());
                log.error(errorMsg);
                throw new CTPException(CTPException.Fault.SYSTEM_ERROR, errorMsg);
            }
        }

        return result;
    }
}
