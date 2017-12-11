package uk.gov.ons.ctp.response.notify.client;

import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClientApi;

/**
 * A simple factory class to create a NotificationClientApi object from a NotifyConfiguration
 */
@Slf4j
public class NotificationClientFactory {

    /**
     * An enum to represent the different possible configurations.  Parsed from notify.debugType application.yml
     * configuration item (or env etc)
     * None - default, uses ConfigurationAwareNotificationClient
     * NotifyClientException - uses an ExceptionThrowingNotificationClient with an optional supplied httpCode
     * RuntimeException - uses RuntimeExceptionThrowingNotificationClient
     */
    public enum DebugType {
        None, NotifyClientException, RuntimeException;
    }

    public static NotificationClientApi getNotificationClient(NotifyConfiguration config){
        NotificationClientApi client = null;
        DebugType type = DebugType.None;
        String debugTypeString = config.getDebugType();

        if (StringUtil.isBlank(debugTypeString) == false){
            type = DebugType.valueOf(debugTypeString);
        }

        switch(type){
            case None:
                client = new ConfigurationAwareNotificationClient(config);
                break;
            case NotifyClientException:
                String httpCode = config.getDebugHttpCode();
                if (StringUtil.isBlank(httpCode) == false){
                    client = new ExceptionThrowingNotificationClient(Integer.parseInt(httpCode));
                } else {
                    client = new ExceptionThrowingNotificationClient();
                }
                break;
            case RuntimeException:
                client = new RuntimeExceptionThrowingNotificationClient();
                break;
        }

        log.info("Creating NotificationClientApi - {} - {}", debugTypeString, client.getClass().getCanonicalName());

        return client;
    }
}
