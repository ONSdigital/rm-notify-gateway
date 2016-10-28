package uk.gov.ons.ctp.response.notify.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.notify.service.impl.NotifyServiceImpl;

import static junit.framework.TestCase.assertTrue;

/**
 * To unit test NotifyServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class NotifyServiceImplTest {

  @InjectMocks
  private NotifyServiceImpl notifyService;

  @Test
  public void testProcess() {
    assertTrue(true);
    // TODO
  }
}
