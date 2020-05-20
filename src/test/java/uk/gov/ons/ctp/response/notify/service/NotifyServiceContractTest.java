package uk.gov.ons.ctp.response.notify.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.gov.ons.ctp.response.notify.service.NotifyService.NOTIFY_EMAIL_SENT;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.ACTION_ID;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.EMAIL_ADDRESS;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.FORENAME;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.SURNAME;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildActionRequest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import io.pactfoundation.consumer.dsl.LambdaDsl;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.notify.client.CommsTemplateClientException;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class NotifyServiceContractTest {

  @Rule
  public PactProviderRuleMk2 mockProvider
    = new PactProviderRuleMk2("comms-template", "localhost", 9182, this);

  @Autowired private NotifyService notifyService;

  @MockBean
  private NotificationClientApi notificationClient;

  @Pact(consumer = "notify-gateway")
  public RequestResponsePact createPact(PactDslWithProvider builder) {
    Map<String, String> responseHeaders = new HashMap<>();
    responseHeaders.put("Content-Type", "application/json");
    Map<String, String> requestHeaders = new HashMap<>();
    responseHeaders.put("Accept", "application/json");
  
    DslPart bodyMatcher = LambdaDsl.newJsonBody(body -> {
      body.stringType("label");
      body.stringType("type");
      body.stringType("uri");
      body.uuid("id");
      body.object("classification", classification -> {
        body.stringType("GEOGRAPHY");
      });
    }).build();

    return builder
      .given("I Request a notify email reminder template")
        .uponReceiving("notify email reminder GET request")
        .path("/templates")
        .headers(requestHeaders)
        .query("LEGAL_BASIS=BEIS&REGION=YY")
        .method("GET")
      .willRespondWith()
        .status(200)
        .headers(responseHeaders)
        .body(bodyMatcher)
      .toPact();
  }

  /**
   * To test the sending of an email.
   *
   * <p>Note: - the PHONENUMBER is null. - the EMAIL_ADDRESS should be amended to your email
   * address. - Also, ensure you use the correct api key and template id.
   *
   * @throws NotificationClientException if GOV.UK Notify issue
   */
  @Test
  @PactVerification
  public void testProcessHappyPathEmail()
      throws NotificationClientException, CommsTemplateClientException {
    ActionFeedback actionFeedback =
        notifyService.process(
            buildActionRequest(ACTION_ID, FORENAME, SURNAME, null, EMAIL_ADDRESS, true));
    assertNotNull(actionFeedback);
    assertEquals(ACTION_ID, actionFeedback.getActionId());
    assertEquals(Outcome.REQUEST_COMPLETED, actionFeedback.getOutcome());
    assertEquals(NOTIFY_EMAIL_SENT, actionFeedback.getSituation());
  }
}
