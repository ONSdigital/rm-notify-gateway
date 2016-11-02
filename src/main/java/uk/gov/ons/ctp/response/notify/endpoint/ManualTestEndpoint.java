package uk.gov.ons.ctp.response.notify.endpoint;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionEvent;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequests;
import uk.gov.ons.ctp.response.notify.message.ActionInstructionPublisher;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The REST endpoint controller for manual tests - To be removed before PROD.
 */
@Path("/manual")
@Produces({"application/json"})
@Slf4j
public class ManualTestEndpoint implements CTPEndpoint {
  private static final BigDecimal LATITYUDE = new BigDecimal("1000.00");
  private static final BigDecimal LONGITUDE = new BigDecimal("1000.00");

  private static final BigInteger CASEID = new BigInteger("1");
  private static final BigInteger UPRN = new BigInteger("201");

  private static final String ACTION_PLAN = "abc";
  private static final String CASEREF = "1";
  private static final String IAC = "123";
  private static final String NOTIFY = "notify";
  private static final String POSTCODE = "PO157RR";
  private static final String QUESTION_SET = "simple";

  @Inject
  private ActionInstructionPublisher actionInstructionPublisher;

  /**
   * To publish an ActionInstruction
   * @param valid true if publishes a valid ActionInstruction
   * @return 200
   */
  @GET
  @Path("/{valid}")
  public final Response publishActionInstruction(@PathParam("valid") final String valid) {
    log.debug("Entering publishActionInstruction with valid = {}", valid);
    actionInstructionPublisher.send(buildActionInstruction(buildTestData(), new Boolean(valid)));
    return Response.status(Response.Status.OK).build();
  }

  /**
   * To build the test data.
   * Key is actionId, Value is forename,surname,phonenumber
   * @return the test data
   */
  private Map<String, String> buildTestData() {
    Map<String, String> testData = new HashMap<>();
    testData.put("1", "Joe,Blogg,07742994131");
    testData.put("2", "Bob,Smith,07742994132");
    testData.put("3", "Al,Simms,07742994133");
    return testData;
  }

  /**
   * To build an ActionInstruction
   * @param dataMap the test data
   * @param valid true if validates against xsd
   * @return the ActionInstruction
   */
  private ActionInstruction buildActionInstruction(Map<String, String> dataMap, boolean valid) {
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
   * To build an ActionRequest
   * @param actionId the actionId
   * @param forename the forename
   * @param surname the surname
   * @param phoneNumber the phone number
   * @param valid true if validates against xsd
   * @return the ActionRequest
   */
  private ActionRequest buildActionRequest(BigInteger actionId, String forename, String surname, String phoneNumber,
                                           boolean valid) {
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
}
