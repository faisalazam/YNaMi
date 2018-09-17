package penetration.pk.lucidxpo.ynami.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Port {
    private int number;
    private State state;

    public Port(final int number) {
        this.number = number;
    }

    public Port(final int number, final State state) {
        this.number = number;
        this.state = state;
    }
}