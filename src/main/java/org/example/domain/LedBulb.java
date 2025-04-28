package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LedBulb implements Bulb {

    private String name;
    private boolean on;
    private boolean broken;
    private int remainingUses;

    public LedBulb(String name) {
        this.name = name;
        this.on = false;
        this.broken = false;
        this.remainingUses = 3;
    }

    @Override
    public void switchOn() {

    }

    @Override
    public void switchOff() {

    }
}
