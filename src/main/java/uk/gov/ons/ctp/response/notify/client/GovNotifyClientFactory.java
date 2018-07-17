package uk.gov.ons.ctp.response.notify.client;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientApi;

public class GovNotifyClientFactory {
  public NotificationClientApi create(NotifyConfiguration config) {
    if (StringUtils.isEmpty(config.getEndpointBaseUrl())) {
      return new NotificationClient(config.getApiKey());
    } else {
      String baseUrl = config.getEndpointBaseUrl().replaceAll("/+$", "");
      return new NotificationClient(config.getApiKey(), baseUrl);
    }
  }
}
