package uk.gov.ons.ctp.response.notify.domain.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.ons.ctp.response.notify.domain.model.Message;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class MessageRepositoryTest {
  
  @Autowired
  private MessageRepository repo;

  @Test
  public void canSaveToRepo() {
    Message message = new Message();
    message.setId(UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff1"));
    message.setNotificationId(UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff2"));
    Message savedMessage = repo.save(message);

    assertNotNull(savedMessage.getMessagePK());
  }

  @Sql({ "classpath:data-h2-test.sql" })
  @Test
  public void canFindById() {
    Message message = repo.findById(UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffffa"));

    assertEquals(UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffffa"), message.getId());
    assertEquals(UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffffb"), message.getNotificationId());
  }
}