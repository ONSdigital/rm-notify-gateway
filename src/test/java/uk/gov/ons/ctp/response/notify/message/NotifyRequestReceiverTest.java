package uk.gov.ons.ctp.response.notify.message;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.ons.ctp.response.notify.domain.model.Message;
import uk.gov.ons.ctp.response.notify.lib.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.service.NotifyService;
import uk.gov.ons.ctp.response.notify.service.ResilienceService;
import uk.gov.service.notify.NotificationClientException;

/** To unit test NotifyRequestReceiverImpl */
@RunWith(MockitoJUnitRunner.class)
public class NotifyRequestReceiverTest {

  @InjectMocks private NotifyRequestReceiver notifyRequestReceiver;

  @Mock private NotifyService notifyService;

  @Mock private ResilienceService resilienceService;

  @Mock private NotifyConfiguration notifyConfiguration;

  private static final String NOTIFY_REQUEST_ID = "de0da3c1-2cad-421a-bddd-054ef374c6ab";
  private static final UUID NOTIFICATION_ID =
      UUID.fromString("f3778220-f877-4a3d-80ed-e8fa7d104563");

  /**
   * Scenario where GOV.UK Notify processes the request OK and the notificationId is stored to our
   * DB
   *
   * @throws NotificationClientException when notifyRequestReceiver does
   */
  @Test
  public void testHappyPath() throws NotificationClientException {
    when(notifyConfiguration.getEnabled()).thenReturn(true);
    when(notifyService.process(any(NotifyRequest.class))).thenReturn(NOTIFICATION_ID);

    NotifyRequest notifyRequest = NotifyRequest.builder().withId(NOTIFY_REQUEST_ID).build();
    notifyRequestReceiver.process(notifyRequest);

    verify(notifyService, times(1)).process(eq(notifyRequest));

    ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
    verify(resilienceService, times(1)).update(argument.capture());
    Message message = argument.getValue();
    assertEquals(NOTIFY_REQUEST_ID, message.getId().toString());
    assertEquals(NOTIFICATION_ID, message.getNotificationId());
  }

  /**
   * Scenario where GOV.UK Notify throws an exception when processing the request
   *
   * @throws NotificationClientException when notifyRequestReceiver does
   */
  @Test
  public void testGovUKNotifyException() throws NotificationClientException {
    when(notifyConfiguration.getEnabled()).thenReturn(true);
    when(notifyService.process(any(NotifyRequest.class)))
        .thenThrow(new NotificationClientException(new Exception()));

    NotifyRequest notifyRequest = NotifyRequest.builder().withId(NOTIFY_REQUEST_ID).build();
    try {
      notifyRequestReceiver.process(notifyRequest);
      fail();
    } catch (NotificationClientException e) {
    }

    verify(notifyService, times(1)).process(eq(notifyRequest));
    verify(resilienceService, times(0)).update(any(Message.class));
  }

  /**
   * Scenario integration with GOV.UK Notify is disabled
   *
   * @throws NotificationClientException when notifyRequestReceiver does
   */
  @Test
  public void testMessageNotSentIfNotifyDisabled() throws NotificationClientException {
    // Given Notify is Disabled
    when(notifyConfiguration.getEnabled()).thenReturn(false);

    NotifyRequest notifyRequest = NotifyRequest.builder().withId(NOTIFY_REQUEST_ID).build();
    notifyRequestReceiver.process(notifyRequest);

    verify(notifyService, times(0)).process(eq(notifyRequest));
    verify(resilienceService, times(0)).update(any(Message.class));
  }
}
