package uk.gov.ons.ctp.response.notify.representation;

import java.util.Date;
import java.util.UUID;

import lombok.*;

/**
 * Domain model object
 */
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
