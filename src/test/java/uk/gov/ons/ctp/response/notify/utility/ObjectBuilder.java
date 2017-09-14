package uk.gov.ons.ctp.response.notify.utility;

import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionEvent;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.SendEmailResponse;
import uk.gov.service.notify.SendSmsResponse;

import java.math.BigDecimal;

public class ObjectBuilder {

  public static final String ACTION_ID = "9a5f2be5-f944-41f9-982c-3517cfcfef3c";
  public static final String EMAIL_ADDRESS = "bp@gmail.com";
  public static final String FORENAME = "Joe";
  public static final String IAC_AS_DISPLAYED_IN_SMS = "123A BC45 6DEF";
  public static final String IAC_AS_STORED_IN_DB = "123abc456def";
  public static final String INVALID_EMAIL_ADDRESS = "@random";
  public static final String INVALID_PHONENUMBER = "0798567515";
  public static final String NOTIFICATION_ID = "067e6162-3b6f-4ae2-a171-2470b63dff00";
  public static final String PHONENUMBER = "07985675147";
  public static final String REFERENCE = "Test text message";
  public static final String SENT = "sent";
  public static final String SMS = "sms";
  public static final String SURNAME = "Blogg";
  public static final String TEMPLATE_ID = "966731dc-ef2e-41ad-a828-8cdd95c81ebc";

  private static final BigDecimal LATITYUDE = new BigDecimal("1000.00");
  private static final BigDecimal LONGITUDE = new BigDecimal("1000.00");

  private static final String ACTION_PLAN = "abc";
  private static final String CASE_ID = "9a5f2be5-f944-41f9-982c-3517cfcfef3c";
  private static final String CASE_REF = "1";
  private static final String CREATED_AT = "2004-12-13T21:39:45.618-08:00";
  private static final String NOTIFY = "notify";
  private static final String POSTCODE = "PO157RR";
  private static final String QUESTION_SET = "simple";
  private static final String RETURN_BY_DATE = "13/09/2017";
  private static final String RU_NAME = "Tesco";
  private static final String SAMPLE_UNIT_REF = "201201201201";
  private static final String SURVEY_NAME = "BRES 2017";
  private static final String SURVEY_REF = "221";
  private static final String TRADING_STYLE = "Ltd";

  /**
   * This builds an ActionInstruction.
   *
   * @param actionId is the actionId
   * @param params is a String containing first name, surname, telephoneNumber, email address
   * @param valid true if valid ActionInstruction
   * @return an ActionInstruction
   */
  public static ActionInstruction buildActionInstruction(String actionId, String params, boolean valid) {
    ActionInstruction actionInstruction = new ActionInstruction();
    String[] paramsArray = params.split(",");
    actionInstruction.setActionRequest(buildActionRequest(actionId, paramsArray[0], paramsArray[1], paramsArray[2],
        paramsArray[3], valid));
    return actionInstruction;
  }

  /**
   * This builds an ActionRequest.
   * @param actionId the actionId
   * @param forename the forename
   * @param surname the surname
   * @param phoneNumber the phoneNumber
   * @param valid true if valid ActionRequest
   * @return
   */
  public static ActionRequest buildActionRequest(String actionId, String forename, String surname, String phoneNumber,
                                                 String emailAddress, boolean valid) {
    ActionRequest actionRequest = new ActionRequest();
    actionRequest.setResponseRequired(true);
    actionRequest.setActionId(actionId);
    ActionContact actionContact = new ActionContact();
    actionContact.setForename(forename);
    actionContact.setSurname(surname);
    actionContact.setPhoneNumber(phoneNumber);
    actionContact.setEmailAddress(emailAddress);
    actionContact.setRuName(RU_NAME);
    actionContact.setTradingStyle(TRADING_STYLE);
    actionRequest.setContact(actionContact);
    actionRequest.setSurveyName(SURVEY_NAME);
    actionRequest.setSurveyRef(SURVEY_REF);
    actionRequest.setReturnByDate(RETURN_BY_DATE);
    if (valid) {
      actionRequest.setActionPlan(ACTION_PLAN);
      actionRequest.setActionType(NOTIFY);
      actionRequest.setQuestionSet(QUESTION_SET);
      ActionAddress actionAddress = new ActionAddress();
      actionAddress.setSampleUnitRef(SAMPLE_UNIT_REF);
      actionAddress.setPostcode(POSTCODE);
      actionAddress.setLatitude(LATITYUDE);
      actionAddress.setLongitude(LONGITUDE);
      actionRequest.setAddress(actionAddress);
      actionRequest.setCaseId(CASE_ID);
      actionRequest.setCaseRef(CASE_REF);
      actionRequest.setIac(IAC_AS_STORED_IN_DB);
      ActionEvent actionEvent = new ActionEvent();
      actionRequest.setEvents(actionEvent);
    }
    return actionRequest;
  }

  public static SendSmsResponse buildSendSmsResponse() {
    return new SendSmsResponse("{\n" +
            "\t\"id\": \"" + NOTIFICATION_ID + "\",\n" +
            "\t\"reference\": \"" + REFERENCE + "\",\n" +
            "\t\"content\": {\n" +
            "\t\t\"body\": \"thebody\"\n" +
            "\t},\n" +
            "\t\"template\": {\n" +
            "\t\t\"id\": \"966731dc-ef2e-41ad-a828-8cdd95c81ebc\",\n" +
            "\t\t\"version\": 1,\n" +
            "\t\t\"uri\": \"theUri\"\n" +
            "\t}\n" +
            "}");
  }

  public static SendEmailResponse buildSendEmailResponse() {
    return new SendEmailResponse("{\n" +
            "\t\"id\": \"" + NOTIFICATION_ID + "\",\n" +
            "\t\"reference\": \"" + REFERENCE + "\",\n" +
            "\t\"content\": {\n" +
            "\t\t\"body\": \"thebody\",\n" +
            "\t\t\"subject\": \"thesubject\"\n" +
            "\t},\n" +
            "\t\"template\": {\n" +
            "\t\t\"id\": \"966731dc-ef2e-41ad-a828-8cdd95c81ebc\",\n" +
            "\t\t\"version\": 1,\n" +
            "\t\t\"uri\": \"theUri\"\n" +
            "\t}\n" +
            "}");
  }

  public static Notification buildNotificationForSMS() {
    return new Notification("{\n" +
            "\t\"id\": \"" + NOTIFICATION_ID + "\",\n" +
            "\t\"reference\": \"" + REFERENCE + "\",\n" +
            "\t\"phone_number\": \"" + PHONENUMBER + "\",\n" +
            "\t\"type\": \"" + SMS + "\",\n" +
            "\t\"template\": {\n" +
            "\t\t\"id\": \"" + TEMPLATE_ID + "\",\n" +
            "\t\t\"version\": 1,\n" +
            "\t\t\"uri\": \"theUri\"\n" +
            "\t},\n" +
            "\t\"body\": \"theBody\",\n" +
            "\t\"status\": \"" + SENT + "\",\n" +
            "\t\"created_at\": \"" + CREATED_AT + "\"\n" +
            "}");
  }
}
