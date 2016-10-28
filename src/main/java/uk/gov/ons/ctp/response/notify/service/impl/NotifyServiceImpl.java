package uk.gov.ons.ctp.response.notify.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.ons.ctp.common.error.CTPException;
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
  private static final String EXCEPTION_NOTIFY_SERVICE = "An error occurred contacting GOV.UK Notify: ";

  @Override
  public void process(ActionInstruction actionInstruction) throws CTPException {
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
      String errorMsg = String.format("%s%s", EXCEPTION_NOTIFY_SERVICE, e.getMessage());
      log.error(errorMsg);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, errorMsg);
    }
  }
}
