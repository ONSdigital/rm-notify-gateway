package uk.gov.ons.ctp.response.notify.message;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.message.JmsHelper;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.notify.utility.ObjectBuilder;

import javax.jms.Connection;
import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;
import static uk.gov.ons.ctp.response.notify.utility.ObjectBuilder.buildTestData;

/**
 * Test focusing on Spring Integration
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActionInstructionPublisherImplITCaseConfig.class)
public class ActionInstructionPublisherImplITCase {

  @Autowired
  ActionInstructionPublisher actionInstructionPublisher;

  @Autowired
  CachingConnectionFactory connectionFactory;

  @Autowired
  DefaultMessageListenerContainer actionInstructionMessageListenerContainer;

  private Connection connection;
  private int initialCounter;

  private static final String INVALID_ACTION_INSTRUCTIONS_QUEUE = "Action.InvalidActionInstructions";

  @Before
  public void setUp() throws JMSException {
    connection = connectionFactory.createConnection();
    connection.start();
    initialCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);
  }

  @After
  public void finishCleanly() throws JMSException {
    connection.close();
  }

  @Test
  public void testSendInvalidActionInstructionWithActionInstructionPublisher() throws Exception {
    ActionInstruction actionInstruction = ObjectBuilder.buildActionInstruction(buildTestData(), false);
    actionInstructionPublisher.send(actionInstruction);

    Thread.sleep(10000L);

    /**
     * We check that the xml invalid queue contains 1 additional message.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);
    assertEquals(1, finalCounter - initialCounter);
  }

  @Test
  public void testSendValidActionInstructionWithActionInstructionPublisher() throws Exception {
    ActionInstruction actionInstruction = ObjectBuilder.buildActionInstruction(buildTestData(), true);
    actionInstructionPublisher.send(actionInstruction);

    Thread.sleep(10000L);

    /**
     * We check that no additional message ends up on the invalid queue
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTIONS_QUEUE);
    assertEquals(0, finalCounter - initialCounter);
  }
}
