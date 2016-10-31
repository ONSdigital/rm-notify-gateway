package uk.gov.ons.ctp.response.notify.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl;
import uk.gov.ons.ctp.response.notify.utility.ObjectBuilder;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl.EXCEPTION_NOTIFY_SERVICE;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildNotification;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildNotificationResponse;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildTestData;

/**
 * To unit test NotifyServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class NotifyServiceImplTest {

  @InjectMocks
  private NotifyServiceImpl notifyService;

  @Mock
  private NotificationClient notificationClient;

  @Test
  public void testProcessHappyPath() throws CTPException, NotificationClientException {
    Mockito.when(notificationClient.sendSms(any(String.class), any(String.class), any(HashMap.class))).thenReturn(buildNotificationResponse());
    Mockito.when(notificationClient.getNotificationById(any(String.class))).thenReturn(buildNotification());

    notifyService.process(ObjectBuilder.buildActionInstruction(buildTestData()));

    verify(notificationClient, times(3)).sendSms(any(String.class), any(String.class), any(HashMap.class));
    verify(notificationClient, times(3)).getNotificationById(any(String.class));
  }

  @Test
  public void testProcessSendSmsException() throws NotificationClientException {
    NotificationClientException exception = new NotificationClientException(new Exception());
    Mockito.when(notificationClient.sendSms(any(String.class), any(String.class), any(HashMap.class))).thenThrow(exception);

    boolean exceptionThrown = false;
    try {
      notifyService.process(ObjectBuilder.buildActionInstruction(buildTestData()));
    } catch(CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertTrue(e.getMessage().startsWith(EXCEPTION_NOTIFY_SERVICE));
    }

    assertTrue(exceptionThrown);
  }
}
