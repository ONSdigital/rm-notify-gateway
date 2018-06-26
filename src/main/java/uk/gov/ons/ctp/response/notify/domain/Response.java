package uk.gov.ons.ctp.response.notify.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {
  private UUID id;
  private UUID templateId;

  private String fromEmail;
  private String fromNumber;
  private String reference;
}
