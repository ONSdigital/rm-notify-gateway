package uk.gov.ons.ctp.response.notify.client;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import uk.gov.ons.ctp.response.notify.config.NotifyConfiguration;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientApi;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

public class ConfigurationAwareNotificationClient extends NotificationClientDecorator {

  private final NotifyConfiguration configuration;

  public ConfigurationAwareNotificationClient(
      NotifyConfiguration config, NotificationClientApi realClient) {
    super(realClient);
    this.configuration = config;
  }

  public ConfigurationAwareNotificationClient(NotifyConfiguration config) {
    this(config, new NotificationClient(config.getApiKey()));
  }

  @Override
  public SendEmailResponse sendEmail(
      String templateId, String emailAddress, Map<String, String> personalisation, String reference)
      throws NotificationClientException {

    boolean addressOverride = this.configuration.getAddressOverride();
    String overrideAddress = this.configuration.getOverrideAddress();

    String actualEmail =
        addressOverride && !StringUtils.isBlank(overrideAddress) ? overrideAddress : emailAddress;

    return getClient().sendEmail(templateId, actualEmail, personalisation, reference);
  }
}
