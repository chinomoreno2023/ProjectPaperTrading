package options.papertrading.models.journal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import options.papertrading.models.person.Person;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "journal")
public class Journal{

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