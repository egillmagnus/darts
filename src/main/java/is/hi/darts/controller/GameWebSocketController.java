package is.hi.darts.controller;

import is.hi.darts.dto.GameUpdateMessage;
import is.hi.darts.model.GameInvite;
import is.hi.darts.model.Player;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    @MessageMapping("/game/score")
    @SendTo("/topic/game-updates")
    public GameUpdateMessage sendScoreUpdate(GameUpdateMessage message) {
        return message; // Broadcasts the message to all clients subscribed to /topic/game-updates
    }

    @MessageMapping("/game/invite")
    @SendTo("/topic/invites")
    public GameInvite sendInviteNotification(GameInvite invite) {
        return invite;
    }
}

