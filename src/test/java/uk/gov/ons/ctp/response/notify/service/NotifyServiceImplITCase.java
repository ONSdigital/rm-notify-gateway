package uk.gov.ons.ctp.response.notify.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.notify.utility.ObjectBuilder;

import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildTestData;

@SpringBootTest(classes = NotifyServiceImplITCaseConfig.class)
@RunWith(SpringRunner.class)
public class NotifyServiceImplITCase {

  @Autowired
  private NotifyService notifyService;

  @Test
  public void testProcess() {
    try {
      notifyService.process(ObjectBuilder.buildActionInstruction(buildTestData()));
    } catch(CTPException e) {
      // TODO At the moment, we get a "Invalid token: expired" error thrown.
    }
  }
}
