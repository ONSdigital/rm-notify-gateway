package uk.gov.ons.ctp.response.notify.domain.model;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.UUID;

/**
 * Domain model object.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message", schema = "notifygatewaysvc")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "messageseq_gen")
    @GenericGenerator(name = "messageseq_gen", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "notifygatewaysvc.messageseq"),
                    @Parameter(name = "increment_size", value = "1")})
    @Column(name = "messagepk")
    private Integer messagePK;

    @Column(name = "id")
    private UUID id;

    @Column(name = "notificationid")
    private UUID notificationId;
}
