package uk.gov.ons.ctp.response.notify.endpoint;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
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
  private static final String DEFAULT_ACTION_PLAN = "abc";
  private static final String DEFAULT_QUESTION_SET = "simple";
  private static final String NOTIFY = "notify";

  @Inject
  private ActionInstructionPublisher actionInstructionPublisher;

  /**
   * To publish an ActionInstruction
   * @return 200
   */
  @GET
  @Path("/{valid}")
  public final Response publishActionInstruction(@PathParam("valid") final String valid) {
    log.debug("Entering publishActionInstruction with valid = {}", valid);
    actionInstructionPublisher.send(buildActionInstruction(buildTestData(), new Boolean(valid)));
    return Response.status(Response.Status.OK).build();
  }

  private Map<String, String> buildTestData() {
    Map<String, String> testData = new HashMap<>();
    testData.put("1", "Joe,07742994131");
    testData.put("2", "Joe,07742994132");
    testData.put("3", "Joe,07742994133");
    return testData;
  }

  private ActionInstruction buildActionInstruction(Map<String, String> dataMap, boolean valid) {
    ActionInstruction actionInstruction = new ActionInstruction();
    ActionRequests actionRequests = new ActionRequests();
    List<ActionRequest> actionRequestList = actionRequests.getActionRequests();

    Set<String> actionIds = dataMap.keySet();
    for (String actionId : actionIds) {
      String value = dataMap.get(actionId);
      String[] params = value.split(",");
      actionRequestList.add(buildActionRequest(new BigInteger(actionId), params[0], params[1], valid));
    }

    actionInstruction.setActionRequests(actionRequests);
    return actionInstruction;
  }

  private ActionRequest buildActionRequest(BigInteger actionId, String contactName, String phoneNumber, boolean valid) {
    ActionRequest actionRequest = new ActionRequest();
    actionRequest.setActionId(actionId);
    actionRequest.setContactName(contactName);
    if (valid) {
      actionRequest.setActionPlan(DEFAULT_ACTION_PLAN);
      actionRequest.setActionType(NOTIFY);
      actionRequest.setQuestionSet(DEFAULT_QUESTION_SET);
    }
    return actionRequest;
  }
}