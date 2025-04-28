package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LedBulb implements Bulb {

    private String name;
    private BulbState state;

    public LedBulb(String name) {
        this.name = name;
        this.state = new BulbState(false, false, 2);
    }

    public LedBulb(String name, boolean on, boolean broken, int remainingUses) {
        this.name = name;
        this.state = new BulbState(on, broken, remainingUses);
    }

    @Override
    public void switchOn() {
        state = switchState(state, BulbCommand.SWITCH_ON);
    }

    @Override
    public void switchOff() {
        state = switchState(state, BulbCommand.SWITCH_OFF);
    }

    public BulbState switchState(BulbState state, BulbCommand command) {
        var event = BulbEvent.NO_EVENT;
        if (BulbCommand.SWITCH_ON.equals(command)) {
            if (!state.broken() && !state.on()) {
                if (state.remainingUses() == 0) {
                    event = BulbEvent.BROKE;
                } else {
                    event = BulbEvent.SWITCHED_ON;
                }
            }
        } else {
            if (!state.broken() && state.on()) {
                event = BulbEvent.SWITCHED_OFF;
            }
        }

        return switch (event) {
            case BROKE -> new BulbState(false, true, 0);
            case SWITCHED_ON -> new BulbState(true, false, state.remainingUses() - 1);
            case SWITCHED_OFF -> new BulbState(false, false, state.remainingUses());
            default -> new BulbState(state.on(), state.broken(), state.remainingUses());
        };
    }

    @Override
    public boolean isOn() {
        return state.on();
    }

    @Override
    public boolean isBroken() {
        return state.broken();
    }
}
