package uk.gov.ons.ctp.response.notify;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.godaddy.logging.LoggingConfigs;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.notify.config.AppConfig;

/** The main application class */
@IntegrationComponentScan
@EnableAsync
@EnableScheduling
@EnableCaching
@ImportResource("springintegration/main.xml")
@SpringBootApplication
public class Application {
  private static final Logger log = LoggerFactory.getLogger(Application.class);

  @Autowired private AppConfig appConfig;

  /**
   * The restTemplate bean injected in REST client classes
   *
   * @return the restTemplate used in REST calls
   */
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  /**
   * The RestExceptionHandler to handle exceptions thrown in our endpoints
   *
   * @return the RestExceptionHandler
   */
  @Bean
  public RestExceptionHandler restExceptionHandler() {
    return new RestExceptionHandler();
  }

  /**
   * Bean used to access comms template service through REST calls
   *
   * @return the service client
   */
  @Bean
  public RestUtility restUtility() {
    return new RestUtility(appConfig.getCommsTemplateService().getConnectionConfig());
  }

  /**
   * This method is the entry point to the Spring Boot application.
   *
   * @param args These are the optional command line arguments
   */
  public static void main(String[] args) {
    log.debug("About to start the Notify Gateway application...");
    SpringApplication.run(Application.class, args);
  }

  @PostConstruct
  public void initJsonLogging() {
    if (appConfig.getLogging().isUseJson()) {
      LoggingConfigs.setCurrent(LoggingConfigs.getCurrent().useJson());
    }
  }
}
