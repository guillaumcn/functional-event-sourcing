package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

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
        var events = decide(state, command);
        return evolveAll(state, events);
    }

    private static List<BulbEvent> decide(BulbState state, BulbCommand command) {
        if (BulbCommand.SWITCH_ON.equals(command)) {
            if (!state.broken() && !state.on()) {
                if (state.remainingUses() == 0) {
                    return List.of(BulbEvent.BROKE);
                } else {
                    return List.of(BulbEvent.SWITCHED_ON);
                }
            }
        } else {
            if (!state.broken() && state.on()) {
                return List.of(BulbEvent.SWITCHED_OFF);
            }
        }
        return Collections.emptyList();
    }

    private static BulbState evolve(BulbState state, BulbEvent event) {
        return switch (event) {
            case BROKE -> new BulbState(false, true, 0);
            case SWITCHED_ON -> new BulbState(true, false, state.remainingUses() - 1);
            case SWITCHED_OFF -> new BulbState(false, false, state.remainingUses());
        };
    }

    private static BulbState evolveAll(BulbState initialState, List<BulbEvent> events) {
        var state = new BulbState(initialState.on(), initialState.broken(), initialState.remainingUses());
        for (BulbEvent event : events) {
            state = evolve(state, event);
        }
        return state;
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
