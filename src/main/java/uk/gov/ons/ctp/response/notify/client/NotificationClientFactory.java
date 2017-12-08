package uk.gov.ons.ctp.response.notify.client;

import jodd.util.StringUtil;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClientApi;

public class NotificationClientFactory {

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

        return client;
    }
}
