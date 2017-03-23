package uk.gov.ons.ctp.response.notify.utility;

import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionEvent;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequests;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.SendSmsResponse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectBuilder {

  public static final BigInteger ACTION_ID = new BigInteger("1");

  public static final String FORENAME = "Joe";
  public static final String IAC_AS_DISPLAYED_IN_SMS = "123A BC45 6DEF";
  public static final String IAC_AS_STORED_IN_DB = "123abc456def";
  public static final String INVALIDPHONENUMBER = "0798567515";
  public static final String PHONENUMBER = "07985675157";
  public static final String SURNAME = "Blogg";

  private static final BigDecimal LATITYUDE = new BigDecimal("1000.00");
  private static final BigDecimal LONGITUDE = new BigDecimal("1000.00");

  private static final Integer CASEID = new Integer("1");
  private static final BigInteger UPRN = new BigInteger("201");

  private static final String ACTION_PLAN = "abc";
  private static final String CASEREF = "1";
  private static final String NOTIFY = "notify";
  private static final String POSTCODE = "PO157RR";
  private static final String QUESTION_SET = "simple";

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
    actionRequest.setResponseRequired(true);
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
      actionRequest.setIac(IAC_AS_STORED_IN_DB);
      ActionEvent actionEvent = new ActionEvent();
      actionRequest.setEvents(actionEvent);
    }
    return actionRequest;
  }

  public static SendSmsResponse buildSendSmsResponse() {
    return new SendSmsResponse("{\n" +
            "\t\"id\": \"067e6162-3b6f-4ae2-a171-2470b63dff00\",\n" +
            "\t\"reference\": \"testReference\",\n" +
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

  public static Notification buildNotification() {
    return new Notification("{\n" +
            "\t\"id\": \"067e6162-3b6f-4ae2-a171-2470b63dff00\",\n" +
            "\t\"type\": \"sms\",\n" +
            "\t\"template\": {\n" +
            "\t\t\"id\": \"966731dc-ef2e-41ad-a828-8cdd95c81ebc\",\n" +
            "\t\t\"version\": 1,\n" +
            "\t\t\"uri\": \"theUri\"\n" +
            "\t},\n" +
            "\t\"body\": \"theBody\",\n" +
            "\t\"status\": \"sent\",\n" +
            "\t\"created_at\": \"2004-12-13T21:39:45.618-08:00\"\n" +
            "}");
  }

  public static Map<String, String> buildTestData() {
    Map<String, String> testData = new HashMap<>();
    testData.put("1", "Joe,Blogg,07742994131");
    testData.put("2", "Bob,Smith,07742994132");
    testData.put("3", "Al,Simms,07742994133");
    return testData;
  }

  /**
   * Note the 2nd phone number with only 3 digits.
   */
  public static Map<String, String> buildTestInvalidData() {
    Map<String, String> testData = new HashMap<>();
    testData.put("1", "Joe,Blogg,07742994131");
    testData.put("2", "Bob,Smith,077");
    testData.put("3", "Al,Simms,07742994133");
    return testData;
  }

  /**
   * Note the space in the phone
   */
  public static Map<String, String> buildTestDataForCtpa1170() {
    Map<String, String> testData = new HashMap<>();
    testData.put("1", "Joe,Blogg,07742 994131");
    return testData;
  }

  public static Map<String, String> buildTestDataMultipleSpacesAndParentheses() {
    Map<String, String> testData = new HashMap<>();
    testData.put("1", "Joe,Blogg,(4)77   42 99 41 31");
    return testData;
  }
}
