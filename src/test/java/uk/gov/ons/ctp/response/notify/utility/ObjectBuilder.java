package uk.gov.ons.ctp.response.notify.utility;

import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionEvent;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequests;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationResponse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectBuilder {

  private static final BigDecimal LATITYUDE = new BigDecimal("1000.00");
  private static final BigDecimal LONGITUDE = new BigDecimal("1000.00");

  public static final BigInteger ACTION_ID = new BigInteger("1");
  private static final BigInteger CASEID = new BigInteger("1");
  private static final BigInteger UPRN = new BigInteger("201");

  private static final String ACTION_PLAN = "abc";
  private static final String CASEREF = "1";
  public static final String FORENAME_KEY = "forename";
  public static final String FORENAME = "Joe";
  public static final String IAC_KEY = "iac";
  public static final String IAC = "123";
  public static final String NOTIFICATION_ID = "1";
  private static final String NOTIFY = "notify";
  public static final String PHONENUMBER = "07742994131";
  public static final String INVALID_PHONENUMBER = "077";
  private static final String POSTCODE = "PO157RR";
  private static final String QUESTION_SET = "simple";
  public static final String SURNAME_KEY = "surname";
  public static final String SURNAME = "Blogg";
  public static final String STATUS = "sent";

  /**
   * This builds an ActionInstruction.
   * @param dataMap is a map where keys are actionIds and values are contactName,telephoneNumber
   * @param valid true if valid ActionInstruction
   * @return an ActionInstruction
   */
  public static ActionInstruction buildActionInstruction(Map<String, String> dataMap, boolean valid) {
    ActionInstruction actionInstruction = new ActionInstruction();
    ActionRequests actionRequests = new ActionRequests();
    List<ActionRequest> actionRequestList = actionRequests.getActionRequests();

    Set<String> actionIds = dataMap.keySet();
    for (String actionId : actionIds) {
      String value = dataMap.get(actionId);
      String[] params = value.split(",");
      actionRequestList.add(buildActionRequest(new BigInteger(actionId), params[0], params[1], params[2], valid));
    }

    actionInstruction.setActionRequests(actionRequests);
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
  public static ActionRequest buildActionRequest(BigInteger actionId, String forename, String surname, String phoneNumber, boolean valid) {
    ActionRequest actionRequest = new ActionRequest();
    actionRequest.setActionId(actionId);
    ActionContact actionContact = new ActionContact();
    actionContact.setForename(forename);
    actionContact.setSurname(surname);
    actionContact.setPhoneNumber(phoneNumber);
    actionRequest.setContact(actionContact);
    if (valid) {
      actionRequest.setActionPlan(ACTION_PLAN);
      actionRequest.setActionType(NOTIFY);
      actionRequest.setQuestionSet(QUESTION_SET);
      ActionAddress actionAddress = new ActionAddress();
      actionAddress.setUprn(UPRN);
      actionAddress.setPostcode(POSTCODE);
      actionAddress.setLatitude(LATITYUDE);
      actionAddress.setLongitude(LONGITUDE);
      actionRequest.setAddress(actionAddress);
      actionRequest.setCaseId(CASEID);
      actionRequest.setCaseRef(CASEREF);
      actionRequest.setIac(IAC);
      ActionEvent actionEvent = new ActionEvent();
      actionRequest.setEvents(actionEvent);
    }
    return actionRequest;
  }

  public static NotificationResponse buildNotificationResponse() {
    return new NotificationResponse("{\n" +
            "\t\"data\": {\n" +
            "\t\t\"notification\": {\n" +
            "\t\t\t\"id\": \"" + NOTIFICATION_ID + "\"\n" +
            "\t\t},\n" +
            "\t\t\"body\": \"abc\",\n" +
            "\t\t\"template_version\": 1\n" +
            "\t}\n" +
            "}");
  }

  public static Notification buildNotification() {
    return new Notification("{\n" +
            "\t\"data\": {\n" +
            "\t\t\"notification\": {\n" +
            "\t\t\t\"id\": \"1\",\n" +
            "\t\t\t\"body\": \"def\",\n" +
            "\t\t\t\"notification_type\": \"test\",\n" +
            "\t\t\t\"template\": {\n" +
            "\t\t\t\t\"id\": \"1\",\n" +
            "\t\t\t\t\"name\": \"test\"\n" +
            "\t\t\t},\n" +
            "\t\t\t\"template_version\": 1,\n" +
            "\t\t\t\"to\": \"tester\",\n" +
            "\t\t\t\"status\": \"" + STATUS + "\",\n" +
            "\t\t\t\"created_at\": \"20110706\"\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}");
  }

  public static Map<String, String> buildTestData() {
    Map<String, String> testData = new HashMap<>();
    testData.put("1", "Joe,Blogg,07742994131");
    testData.put("2", "Bob,Smith,07742994132");
    testData.put("3", "Al,Simms,07742994133");
    return testData;
  }

  public static Map<String, String> buildTestDataInvalidPhoneNumbers() {
    Map<String, String> testData = new HashMap<>();
    testData.put("1", "Joe,Blogg,077");
    testData.put("2", "Bob,Smith,078");
    testData.put("3", "Al,Simms,079");
    return testData;
  }
}
