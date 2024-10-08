package options.papertrading.models.person;

import lombok.Data;
import lombok.EqualsAndHashCode;
import options.papertrading.models.journal.Journal;
import options.papertrading.models.portfolio.Portfolio;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Data
@Table(name = "persons")
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username")
    @Size(min = 3, max = 30, message = "Имя должно быть от 3 до 30 символов")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    @NotEmpty(message = "Вы забыли ввести почту")
    @Email(message = "Вы ввели некорректную почту")
    @Size(max = 50, message = "Длина почты не должна превышать 30 символов")
    private String email;

    @Column(name = "role")
    private String role;

    @Column(name = "current_net_position")
    private double currentNetPosition;

    @Column(name = "open_limit")
    private double openLimit;

    @OneToMany(mappedBy = "owner")
    @EqualsAndHashCode.Exclude
    private List<Portfolio> optionsInPortfolio;

    @OneToMany(mappedBy = "owner")
    @EqualsAndHashCode.Exclude
    private List<Journal> journal;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "is_active")
    private boolean isActive;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", currentNetPosition=" + currentNetPosition +
                ", openLimit=" + openLimit +
                ", activationCode='" + activationCode + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}