package uk.gov.ons.ctp.response.notify.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Created by stevee on 17/07/2017.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class SendEmailDTO {

  UUID notificationId;

  String reference;

  UUID templateId;

  Integer templateVersion;

  String fromEmail;


}
