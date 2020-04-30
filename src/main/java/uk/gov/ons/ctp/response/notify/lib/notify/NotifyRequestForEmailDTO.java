package uk.gov.ons.ctp.response.notify.lib.notify;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** Domain model object */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class NotifyRequestForEmailDTO extends NotifyRequestDTO {

  public static final String EMAIL_ADDRESS_REGEX = ".*@.*";

  @NotNull
  @Pattern(regexp = EMAIL_ADDRESS_REGEX)
  private String emailAddress;
}
