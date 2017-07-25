package uk.gov.ons.ctp.response.notify.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.notify.domain.SendSmsResponse;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.domain.repository.MessageRepository;
import uk.gov.ons.ctp.response.notify.message.NotifyRequestPublisher;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.ResilienceService;

import java.util.UUID;

@Slf4j
@Service
public class ResilienceServiceImpl implements ResilienceService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private NotifyRequestPublisher notifyRequestPublisher;

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
}
