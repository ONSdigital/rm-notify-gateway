package uk.gov.ons.ctp.response.notify.service.impl;

import org.junit.Test;

import static org.testng.AssertJUnit.assertEquals;

public class NotifyServiceImplTest {

    @Test
    public void testNotificationTypeNull(){
        NotifyServiceImpl.NotificationType notificationType =
                NotifyServiceImpl.NotificationType.getNotificationTypeFromActionType(null);

        assertEquals(NotifyServiceImpl.NotificationType.None, notificationType);
    }

    @Test
    public void testNotificationTypeNone(){
        NotifyServiceImpl.NotificationType notificationType =
                NotifyServiceImpl.NotificationType.getNotificationTypeFromActionType("BOGUS");

        assertEquals(NotifyServiceImpl.NotificationType.None, notificationType);
    }

    @Test
    public void testNotificationTypeBRESNotification(){
        NotifyServiceImpl.NotificationType notificationType =
                NotifyServiceImpl.NotificationType.getNotificationTypeFromActionType("BSSNE");

        assertEquals(NotifyServiceImpl.NotificationType.None, notificationType);
    }
}
