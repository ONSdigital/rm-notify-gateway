package uk.gov.ons.ctp.response.notify.config;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Config POJO for Swagger UI Generation
 */
@Data
@NoArgsConstructor
public class SwaggerSettings {

  private Boolean swaggerUiActive;
  private String groupName;
  private String title;
  private String description;
  private String version;

}
