package uk.gov.ons.ctp.response.notify.util;

import java.util.Date;
import java.util.Optional;

import org.joda.time.DateTime;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import uk.gov.ons.ctp.response.notify.lib.notify.NotificationDTO;
import uk.gov.ons.ctp.response.notify.lib.notify.NotifyRequest;
import uk.gov.ons.ctp.response.notify.lib.notify.NotifyRequestForEmailDTO;
import uk.gov.service.notify.Notification;

@Mapper(builder = @Builder(disableBuilder = true))
public interface NotifyRequestMapper {
  
  NotifyRequestMapper INSTANCE = Mappers.getMapper(NotifyRequestMapper.class);

  /**
   * Theres a mismatch between NotifyRequestForEmailDTO and NotifyRequest,
   * personalisation is a Map for one and a String for the other but it doesnt appear to
   * be used anywhere so it can be deleted as part of the culling
   * @param notifyRequest
   */
  @Mapping(target="personalisation", ignore=true)
  NotifyRequest mapToNotifyRequest(NotifyRequestForEmailDTO notifyRequest);

  @Mappings({
    @Mapping(source="createdAt", target="createdAt", qualifiedByName="jodaTimeToDate"),
    @Mapping(source="sentAt", target="sentAt", qualifiedByName="jodaTimeToDate"),
    @Mapping(source="completedAt", target="completedAt", qualifiedByName="jodaTimeToDate"),
    @Mapping(source="reference", target="reference", qualifiedByName="optionalToString"),
    @Mapping(source="emailAddress", target="emailAddress", qualifiedByName="optionalToString"),
    @Mapping(source="phoneNumber", target="phoneNumber", qualifiedByName="optionalToString")
  })
  NotificationDTO mapToNotificationDTO(Notification notification);

  @Named("jodaTimeToDate")
  public static Date jodaDateToDate(Optional<DateTime> dateTime) {
    return dateTime.map(DateTime::toDate).orElse(null);
  }

  @Named("optionalToString")
  public static String optionalToString(Optional<String> str) {
    return str.orElse(null);
  }
}