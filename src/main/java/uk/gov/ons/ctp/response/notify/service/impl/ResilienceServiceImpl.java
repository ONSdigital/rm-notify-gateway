package uk.gov.ons.ctp.response.notify.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.notify.domain.Response;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.domain.repository.MessageRepository;
import uk.gov.ons.ctp.response.notify.message.NotifyRequestPublisher;
//import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.ResilienceService;

import java.util.UUID;

@Slf4j
@Service
public class ResilienceServiceImpl {
//    implements ResilienceService {

//    @Autowired
//    private MessageRepository messageRepository;
//
//    @Autowired
//    private NotifyRequestPublisher notifyRequestPublisher;

//    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
//    @Override
//    public Response process(NotifyRequest notifyRequest) {
//        String templateId = notifyRequest.getTemplateId();
//        String phoneNumber = notifyRequest.getPhoneNumber();
//        String emailAddress = notifyRequest.getEmailAddress();
//        log.debug("Entering process with censusUacSmsTemplateId {} - phoneNumber {} - emailAddress {}", templateId, phoneNumber,
//                emailAddress);
//
//        UUID theId = UUID.randomUUID();
//        messageRepository.save(Message.builder().id(theId).build());
//        log.debug("Message persisted with id {}", theId);
//
//        notifyRequest.setId(theId.toString());
//        notifyRequestPublisher.send(notifyRequest);
//        log.debug("notifyRequest {} now on queue", notifyRequest);
//
//        return Response.builder()
//                .id(theId)
//                .reference(notifyRequest.getReference())
//                .templateId(UUID.fromString(templateId))
//                .fromNumber(phoneNumber)
//                .fromEmail(emailAddress)
//                .build();
//    }

//    @Override
//    public void update(Message message) {
//        log.debug("Entering update with message {}", message);
//        Message existingMessage = messageRepository.findById(message.getId());
//        log.debug("existingMessage is {}", existingMessage);
//        if (existingMessage != null) {
//            message.setMessagePK(existingMessage.getMessagePK());
//            messageRepository.saveAndFlush(message);
//        } else {
//            // We should never come here as prior to the request being sent to GOV.UK Notify, a message was stored in
//            // the DB.
//            log.error("No existing message found to update with notificationID from GOV.UK Notify");
//        }
//    }

//    @Override
//    public Message findMessageById(UUID id) {
//        log.debug("Entering findMessageById with id {}", id);
//        return messageRepository.findById(id);
//    }
}
