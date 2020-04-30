package uk.gov.ons.ctp.response.notify.lib.notify;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain model object */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ResponseDTO {
  private UUID id;
  private UUID templateId;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String fromEmail;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String fromNumber;

  private String reference;
}
