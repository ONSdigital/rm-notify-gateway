package uk.gov.ons.ctp.response.notify.lib.notify;

import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain model object */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public abstract class NotifyRequestDTO {

  private String reference;

  private Map<String, String> personalisation;
}
