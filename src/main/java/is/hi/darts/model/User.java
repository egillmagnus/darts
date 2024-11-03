package is.hi.darts.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class User implements UserDetails {

    @Id  // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private double threeDartAverage;

    @Column(nullable = false)
    private Long totalRounds;

    @ElementCollection
    @CollectionTable(name = "user_previous_games", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "previous_game")
    private List<String> previousGames = new java.util.ArrayList<>();

    // Default constructor for JPA
    public User() {}

    public User(Long id, String email, String password, String username) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.threeDartAverage = 0.0;
        this.totalRounds = 0L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getDisplayName() {
        return username;
    }

    public void setDisplayName(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password; // Return the hashed password
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setThreeDartAverage(Double threeDartAverage){
        this.threeDartAverage = threeDartAverage;
    }

    public double getThreeDartAverage(){
        return threeDartAverage;
    }

    public Long getTotalRounds(){
        return totalRounds;
    }

    public void setTotalRounds(Long totalRounds){
        this.totalRounds = totalRounds;
    }

    public List<String> getPreviousGames() {
        return previousGames;
    }

    public void addPreviousGame(String game) {
        previousGames.add(game);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
