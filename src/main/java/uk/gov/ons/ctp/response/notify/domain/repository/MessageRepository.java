package uk.gov.ons.ctp.response.notify.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.notify.domain.model.Message;

import java.util.UUID;

/**
 * JPA Data Repository
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    /**
     * Find message by UUID
     * @param id the UUID
     * @return the associated Message
     */
    Message findById(UUID id);
}
