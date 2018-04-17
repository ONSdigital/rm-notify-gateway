package uk.gov.ons.ctp.response.notify.service.impl;

import org.junit.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Class to hold unit tests for NotifyServiceImpl
 */
public class NotifyServiceImplTest {

    @Test
    public void testNotificationTypeNull() {
        NotifyServiceImpl.NotificationType notificationType =
                NotifyServiceImpl.NotificationType.getNotificationTypeFromActionType(null);

        assertEquals(NotifyServiceImpl.NotificationType.None, notificationType);
    }

    @Test
    public void testNotificationTypeNone() {
        NotifyServiceImpl.NotificationType notificationType =
                NotifyServiceImpl.NotificationType.getNotificationTypeFromActionType("BOGUS");

        assertEquals(NotifyServiceImpl.NotificationType.None, notificationType);
    }

    @Test
    public void testNotificationTypeBRESNotification() {
        NotifyServiceImpl.NotificationType notificationType =
                NotifyServiceImpl.NotificationType.getNotificationTypeFromActionType("BSSNE");

        assertEquals(NotifyServiceImpl.NotificationType.Notification, notificationType);
    }

    @Test
    public void testNotificationTypeQBSNotification() {
        NotifyServiceImpl.NotificationType notificationType =
                NotifyServiceImpl.NotificationType.getNotificationTypeFromActionType("BSNE");

        assertEquals(NotifyServiceImpl.NotificationType.Notification, notificationType);
    }

    @Test
    public void testNotificationTypeQBSReminder() {
        NotifyServiceImpl.NotificationType notificationType =
                NotifyServiceImpl.NotificationType.getNotificationTypeFromActionType("BSRE");

        assertEquals(NotifyServiceImpl.NotificationType.Reminder, notificationType);
    }
}
