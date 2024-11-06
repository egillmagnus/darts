package is.hi.darts.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
public class Leg {

    private int startIndex;

    @Setter
    private Integer endIndex;

    @Setter
    private Long winnerPlayerId;

    public Leg(int startIndex) {
        this.startIndex = startIndex;
    }

    protected Leg() {}
}
