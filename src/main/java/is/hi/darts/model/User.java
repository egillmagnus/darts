package is.hi.darts.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class User {

    @Id  // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment ID
    private Long id;

    @Column(nullable = false, unique = true)  // Email must be unique
    private String email;

    @Column(nullable = false)  // Password is required
    private String password;

    @Column(nullable = false)  // Username is required but not unique
    private String username;

    @ElementCollection
    @CollectionTable(name = "user_previous_games", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "previous_game")
    private List<String> previousGames = new ArrayList<>();

    // Default constructor for JPA
    public User() {}

    public User(Long id, String email, String password, String username) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    // Getters and Setters for id, email, password, and username
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Methods for managing previous games
    public List<String> getPreviousGames() {
        return previousGames;
    }

    public void addPreviousGame(String game) {
        previousGames.add(game);
    }
}
