package uk.gov.ons.ctp.response.notify.utility;

import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequests;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationResponse;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectBuilder {
  private static final String DEFAULT_ACTION_PLAN = "abc";
  private static final String DEFAULT_QUESTION_SET = "simple";
  private static final String NOTIFY = "notify";

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
      actionRequest.setActionPlan(DEFAULT_ACTION_PLAN);
      actionRequest.setActionType(NOTIFY);
      actionRequest.setQuestionSet(DEFAULT_QUESTION_SET);
    }
    return actionRequest;
  }

  public static NotificationResponse buildNotificationResponse() {
    return new NotificationResponse("{\n" +
            "\t\"data\": {\n" +
            "\t\t\"notification\": {\n" +
            "\t\t\t\"id\": \"1\"\n" +
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
            "\t\t\t\"status\": \"sent\",\n" +
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
}
