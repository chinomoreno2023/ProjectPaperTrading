package options.papertrading.models.person;

import lombok.Data;
import options.papertrading.models.portfolio.Portfolio;
import org.hibernate.annotations.Cascade;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
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
    @NotEmpty(message = "Имя не может быть пустым")
    @Size(min = 3, max = 30, message = "Name should be between 3 and 30 characters")
    private String username;

    @NotEmpty
    @Size(min = 3, message = "password should be longer than 2")
    @Column(name = "password")
    private String password;

    @Column(name = "email")
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    @Size(min = 3, max = 50, message = "Name should be between 3 and 50 characters")
    private String email;

    @Column(name = "role")
    private String role;

    @Column(name = "current_net_position")
    private double currentNetPosition;

    @Column(name = "open_limit")
    private double openLimit;

    @OneToMany(mappedBy = "owner")
    private List<Portfolio> optionsInPortfolio;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "is_active")
    private boolean isActive;
}