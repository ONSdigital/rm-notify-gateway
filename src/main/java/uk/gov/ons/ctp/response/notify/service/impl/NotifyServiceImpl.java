package uk.gov.ons.ctp.response.notify.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.notify.service.NotifyService;

import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationResponse;

import javax.inject.Inject;
import java.util.HashMap;

@Slf4j
public class NotifyServiceImpl implements NotifyService {

  @Value("${TEMPLATE_ID}")
  private String templateId;

  @Inject
  private NotificationClient notificationClient;

  private static final String FIRST_NAME = "firstName";
  private static final String IAC = "iac";

  @Override
  public void process(ActionInstruction actionInstruction) {
    try {
      HashMap<String, String> personalisation = new HashMap<>();
      personalisation.put(FIRST_NAME, "John");
      personalisation.put(IAC, "ABCD EFGH IJKL");
      NotificationResponse response = notificationClient.sendSms(templateId, "07985675158", personalisation);
      String notificationId = response.getNotificationId();
      Notification notification = notificationClient.getNotificationById(notificationId);
      String status = notification.getStatus();
      log.debug("status = {}", status);
    } catch (NotificationClientException e) {
      log.debug("there was an issue - msg = {}", e.getMessage());
    }
  }
}
