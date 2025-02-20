package options.papertrading.models.person;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import options.papertrading.models.journal.Journal;
import options.papertrading.models.portfolio.Portfolio;

import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "persons")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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