package uk.gov.ons.ctp.response.notify.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.service.NotifyService;

import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationResponse;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

/**
 * The service implementation for NotifyService
 */
@Slf4j
@Named
public class NotifyServiceImpl implements NotifyService {

  @Value("${TEMPLATE_ID}")
  private String templateId;

  @Inject
  private NotificationClient notificationClient;

  private static final String CONTACT_NAME = "contactName";
  private static final String IAC = "iac";

  public static final String EXCEPTION_NOTIFY_SERVICE = "An error occurred contacting GOV.UK Notify: ";

  @Override
  public void process(ActionInstruction actionInstruction) throws CTPException {
    log.debug("Entering process with actionInstruction {}", actionInstruction);
    try {
      List<ActionRequest> actionRequests = actionInstruction.getActionRequests().getActionRequests();
      HashMap<String, String> personalisation = new HashMap<>();
      NotificationResponse response;
      String notificationId, status;
      Notification notification;
      BigInteger actionId;
      for (ActionRequest actionRequest :  actionRequests) {
        actionId = actionRequest.getActionId();
        personalisation.put(CONTACT_NAME, actionRequest.getContactName());
        personalisation.put(IAC, actionRequest.getIac());
        // TODO replace hardcoded phone with data from actionRequest
        String phoneNumber = "07985675158";
        log.debug("About to invoke sendSms with templateId {} - phone number {} - personalisation {} for actionId = {}",
                templateId, phoneNumber, personalisation, actionId);
        response = notificationClient.sendSms(templateId, phoneNumber, personalisation);
        notificationId = response.getNotificationId();
        notification = notificationClient.getNotificationById(notificationId);
        status = notification.getStatus();
        log.debug("status = {} for actionId = {}", status, actionId);
      }
    } catch (NotificationClientException e) {
      String errorMsg = String.format("%s%s", EXCEPTION_NOTIFY_SERVICE, e.getMessage());
      log.error(errorMsg);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, errorMsg);
    }
  }

  /**
   * To set the Template ID
   * @param aTemplateId the Template ID
   * */
  public void setTemplateId(String aTemplateId) {
    templateId = aTemplateId;
  }
}
