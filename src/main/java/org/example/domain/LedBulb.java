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
        state = switchState(state, true);
    }

    @Override
    public void switchOff() {
        state = switchState(state, false);
    }

    public BulbState switchState(BulbState state, boolean on) {
        if (on) {
            if (!state.broken() && !state.on()) {
                if (state.remainingUses() == 0) {
                    return new BulbState(false, true, 0);
                } else {
                    return new BulbState(true, false, state.remainingUses() - 1);
                }
            }
        } else {
            if (!state.broken() && state.on()) {
                return new BulbState(false, false, state.remainingUses());
            }
        }
        return new BulbState(state.on(), state.broken(), state.remainingUses());
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
