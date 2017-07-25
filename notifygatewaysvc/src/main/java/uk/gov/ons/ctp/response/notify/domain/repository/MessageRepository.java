package uk.gov.ons.ctp.response.notify.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.notify.domain.model.Message;

/**
 * JPA Data Repository.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
}
