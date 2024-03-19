package options.papertrading.models.journal;

import lombok.Data;
import options.papertrading.models.person.Person;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "journal")
public class Journal {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date_and_time")
    private LocalDateTime dateAndTime;

    @Column(name = "option_name")
    private String optionName;

    @Column(name = "volume")
    private int volume;

    @Column(name = "price")
    private double price;

    @Column(name = "buy_or_write")
    private int buyOrWrite;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person owner;
}