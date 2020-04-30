package uk.gov.ons.ctp.response.notify.lib.notify;

import java.util.Date;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain model object */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class NotificationDTO {
  private UUID id;
  private String reference;
  private String emailAddress;
  private String phoneNumber;
  private String notificationType;
  private String status;
  private UUID templateId;
  private int templateVersion;
  private Date createdAt;
  private Date sentAt;
  private Date completedAt;
}
