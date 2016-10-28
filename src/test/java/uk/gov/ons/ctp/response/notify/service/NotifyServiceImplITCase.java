package uk.gov.ons.ctp.response.notify.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

@SpringBootTest(classes = NotifyServiceImplITCaseConfig.class)
@RunWith(SpringRunner.class)
public class NotifyServiceImplITCase {

  @Autowired
  private NotifyService notifyService;

  @Test
  public void testProcess() {
    ActionInstruction actionInstruction = new ActionInstruction();
    notifyService.process(actionInstruction);
  }
}
