package options.papertrading.models.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import options.papertrading.models.portfolio.Portfolio;
import org.hibernate.annotations.Cascade;
import java.util.List;

@Entity
@Table(name = "persons")
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotEmpty(message = "Имя не может быть пустым")
    @Size(min = 3, max = 30, message = "Name should be between 3 and 30 characters")
    private String name;

    @Column(name = "email")
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    @Size(min = 3, max = 50, message = "Name should be between 3 and 50 characters")
    private String email;

    @Column(name = "current_net_position")
    private double currentNetPosition;

    @Column(name = "open_limit")
    private double openLimit;

    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @OneToMany(mappedBy = "owner")
    private List<Portfolio> optionsInPortfolio;

    public Person() { }

    public Person(String name, String email, double currentNetPosition, double openLimit) {
        this.name = name;
        this.email = email;
        this.currentNetPosition = currentNetPosition;
        this.openLimit = openLimit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getCurrentNetPosition() {
        return currentNetPosition;
    }

    public void setCurrentNetPosition(double currentNetPosition) {
        this.currentNetPosition = currentNetPosition;
    }

    public double getOpenLimit() {
        return openLimit;
    }

    public void setOpenLimit(double openLimit) {
        this.openLimit = openLimit;
    }

    public List<Portfolio> getOptionsInPortfolio() {
        return optionsInPortfolio;
    }

    public void setOptionsInPortfolio(List<Portfolio> optionsInPortfolio) {
        this.optionsInPortfolio = optionsInPortfolio;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", currentNetPosition=" + currentNetPosition +
                ", openLimit=" + openLimit +
                ", optionsInPortfolio=" + optionsInPortfolio +
                '}';
    }
}