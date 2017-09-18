package uk.gov.ons.ctp.response.notify.service.impl;

import static uk.gov.ons.ctp.response.notify.message.impl.ActionInstructionReceiverImpl.SITUATION_MAX_LENGTH;

import com.google.common.base.Splitter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import liquibase.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.ons.ctp.response.notify.message.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.ons.ctp.response.notify.util.InternetAccessCodeFormatter;
import uk.gov.service.notify.*;

/**
 * The service implementation for NotifyService
 */
@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private NotifyConfiguration notifyConfiguration;

    @Autowired
    private NotificationClient notificationClient;

    public static final String FIRSTNAME_KEY = "firstname";
    public static final String IAC_KEY = "iac";
    public static final String LASTNAME_KEY = "lastname";
    public static final String REPORTING_UNIT_REF_KEY = "reporting unit reference";
    public static final String RETURN_BY_DATE_KEY = "return by date";
    public static final String RU_NAME_KEY = "RU name";
    public static final String SURVEY_ID_KEY = "survey id";
    public static final String SURVEY_NAME_KEY = "survey name";
    public static final String TRADING_STYLE_KEY = "trading style";

    public static final String NOTIFY_EMAIL_SENT = "Notify Email Sent";
    public static final String NOTIFY_SMS_NOT_SENT = "Notify Sms Not Sent";
    public static final String NOTIFY_SMS_SENT = "Notify Sms Sent";

    @Override
    public ActionFeedback process(ActionRequest actionRequest) throws NotificationClientException {
        String actionId = actionRequest.getActionId();
        log.debug("Entering process with actionId {}", actionId);

        ActionFeedback actionFeedback = null;

        ActionContact actionContact = actionRequest.getContact();
        String phoneNumber = actionContact.getPhoneNumber();
        if (!StringUtils.isEmpty(phoneNumber)) { // TODO Switch used for BRES
          actionFeedback = processSms(actionRequest);
        } else {
          actionFeedback = processEmail(actionRequest);
        }

        return actionFeedback;
    }

    @Override
    public UUID process(NotifyRequest notifyRequest) throws NotificationClientException {
        String phoneNumber = notifyRequest.getPhoneNumber();
        String emailAddress = notifyRequest.getEmailAddress();
        String templateId = notifyRequest.getTemplateId();
        String reference = notifyRequest.getReference();
        String personalisation = notifyRequest.getPersonalisation();
        Map<String, String> personalisationMap = buildMapFromString(personalisation);

        if (!StringUtils.isEmpty(phoneNumber)) {
            log.debug("About to invoke sendSms with templateId {} - phone number {} - "
                    + "personalisationMap {}", templateId, phoneNumber, personalisationMap);
            SendSmsResponse response = notificationClient.sendSms(templateId, phoneNumber, personalisationMap,
                    reference);
            log.debug("response = {}", response);
            return response.getNotificationId();
        } else {
            // The xsd enforces to have either a phoneNumber OR an emailAddress
            log.debug("About to invoke sendEmail with templateId {} - emailAddress {} - personalisationMap "
                    + "{}", templateId , emailAddress, personalisationMap);
            SendEmailResponse response = notificationClient.sendEmail(templateId, emailAddress,
                personalisationMap, reference);
            log.debug("response = {}", response);
            return response.getNotificationId();
        }
    }

    @Override
    public Notification findNotificationById(UUID notificationId) throws NotificationClientException {
        return notificationClient.getNotificationById(notificationId.toString());
    }

    /**
     * Transform the string built originally by orika into a Map
     *
     * An example is {iac=ABCD-EFGH-IJKL-MNOP, otherfield=other,value} which was built from
     * "personalisation": {"iac":"ABCD-EFGH-IJKL-MNOP", "otherfield":"other,value"}
     *
     * TODO Other option = find a way to define a notifyRequest.xsd so a Map field ends up in NotifyRequest.java
     * TODO http://blog.bdoughan.com/2013/03/jaxb-and-javautilmap.html
     * TODO http://blog.bdoughan.com/2011/08/xml-schema-to-java-generating.html
     *
     * @param personalisation the string built originally by orika
     * @return the Map
     */
    private Map<String, String> buildMapFromString(String personalisation) {
        Map<String, String> result = null;

        if (!StringUtils.isEmpty(personalisation)) {
            try {
                result = Splitter.on(", ").withKeyValueSeparator("=").split(personalisation.substring(1,
                        personalisation.length() - 1));
            } catch (java.lang.IllegalArgumentException e) {
                log.error("Unexpected personalisation - message is {} - cause is {}", e.getMessage(), e.getCause());
            }
        }

        return result;
    }

    /**
     * To process actionRequest for SMS
     *
     * @param actionRequest to process for SMS
     * @return the ActionFeedback
     * @throws NotificationClientException
     */
    private ActionFeedback processSms(ActionRequest actionRequest) throws NotificationClientException {
        String actionId = actionRequest.getActionId();
        ActionContact actionContact = actionRequest.getContact();
        String phoneNumber = actionContact.getPhoneNumber();

        Map<String, String> personalisation = new HashMap<>();
        personalisation.put(IAC_KEY, InternetAccessCodeFormatter.externalize(actionRequest.getIac()));

        String templateId = notifyConfiguration.getCensusUacSmsTemplateId();
        log.debug("About to invoke sendSms with censusUacSmsTemplateId {} - phone number {} - personalisation {}"
            + " for actionId = {}", templateId, phoneNumber, personalisation, actionId);

        SendSmsResponse response = notificationClient.sendSms(templateId, phoneNumber, personalisation, null);
        log.debug("status = {} for actionId = {}", notificationClient.getNotificationById(
            response.getNotificationId().toString()).getStatus(), actionId);

        return new ActionFeedback(actionId,
            NOTIFY_SMS_SENT.length() <= SITUATION_MAX_LENGTH ?
                NOTIFY_SMS_SENT : NOTIFY_SMS_SENT.substring(0, SITUATION_MAX_LENGTH),
            Outcome.REQUEST_COMPLETED);
    }

    /**
     * To process actionRequest for Email
     *
     * @param actionRequest to process for Email
     * @throws NotificationClientException
     */
    private ActionFeedback processEmail(ActionRequest actionRequest) throws NotificationClientException {
        String actionId = actionRequest.getActionId();
        ActionContact actionContact = actionRequest.getContact();

        Map<String, String> personalisation = new HashMap<>();
        String emailAddress = actionContact.getEmailAddress();
        personalisation.put(REPORTING_UNIT_REF_KEY, actionRequest.getAddress().getSampleUnitRef());
        personalisation.put(SURVEY_NAME_KEY, actionRequest.getSurveyName());
        personalisation.put(SURVEY_ID_KEY, actionRequest.getSurveyRef());
        personalisation.put(FIRSTNAME_KEY, actionContact.getForename());
        personalisation.put(LASTNAME_KEY, actionContact.getSurname());
        personalisation.put(RU_NAME_KEY, actionContact.getRuName());
        personalisation.put(TRADING_STYLE_KEY, actionContact.getTradingStyle());
        personalisation.put(RETURN_BY_DATE_KEY, actionRequest.getReturnByDate());

        String templateId = notifyConfiguration.getOnsSurveysRasEmailReminderTemplateId();
        log.debug("About to invoke sendEmail with onsSurveysRasEmailReminderTemplateId {} - emailAddress {} - "
            + "personalisation {} for actionId = {}", templateId, emailAddress, personalisation, actionId);

        SendEmailResponse response = notificationClient.sendEmail(templateId, emailAddress,
            personalisation, null);
        Notification notif = notificationClient.getNotificationById(response.getNotificationId().toString());
        log.debug("status = {} for actionId = {}", notif.getStatus(), actionId);

        return new ActionFeedback(actionId,
            NOTIFY_EMAIL_SENT.length() <= SITUATION_MAX_LENGTH ?
                NOTIFY_EMAIL_SENT : NOTIFY_EMAIL_SENT.substring(0, SITUATION_MAX_LENGTH),
            Outcome.REQUEST_COMPLETED);
    }
}
