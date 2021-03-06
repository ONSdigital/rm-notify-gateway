package uk.gov.ons.ctp.response.notify.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.notify.lib.common.RestUtilityConfig;

@Data
@NoArgsConstructor
public class CommsTemplateService {
  private RestUtilityConfig connectionConfig;
  private String templateByClassifiersPath;
}
