package is.hi.darts.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GameInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long gameId;

    @Column(nullable = false)
    private Long userId; // The ID of the user being invited

    @Column(nullable = false)
    private Long inviterId; // The ID of the user who sent the invite

    private LocalDateTime invitationTime = LocalDateTime.now(); // Time of the invitation

    public GameInvite() {}

    public GameInvite(Long gameId, Long userId, Long inviterId) {
        this.gameId = gameId;
        this.userId = userId;
        this.inviterId = inviterId;
        this.invitationTime = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getInviterId() {
        return inviterId;
    }

    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }

    public LocalDateTime getInvitationTime() {
        return invitationTime;
    }

    public void setInvitationTime(LocalDateTime invitationTime) {
        this.invitationTime = invitationTime;
    }
}
