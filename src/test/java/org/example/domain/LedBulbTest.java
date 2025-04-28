package org.example.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LedBulbTest {

    private static final String BULB_NAME = "Bulb name";

    @Test
    public void switchOnBulbAlreadyOn_mustNotChange() {
        var bulb = new LedBulb(BULB_NAME, true, false, 3);
        bulb.switchOn();
        assertTrue(bulb.isOn());
        assertFalse(bulb.isBroken());
    }

    @Test
    public void switchOnBrokenBulb_mustStayOffAndStayBroken() {
        var bulb = new LedBulb(BULB_NAME, false, true, 0);
        bulb.switchOn();
        assertFalse(bulb.isOn());
        assertTrue(bulb.isBroken());
    }

    @Test
    public void switchOnBulbWithNoRemainingUses_mustBroke() {
        var bulb = new LedBulb(BULB_NAME, false, false, 0);
        bulb.switchOn();
        assertFalse(bulb.isOn());
        assertTrue(bulb.isBroken());
    }

    @Test
    public void switchOnBulbWithRemainingUses_mustTurnOn() {
        var bulb = new LedBulb(BULB_NAME, false, false, 2);
        bulb.switchOn();
        assertTrue(bulb.isOn());
        assertFalse(bulb.isBroken());
    }

    @Test
    public void switchOffBulbAlreadyOff_mustNotChange() {
        var bulb = new LedBulb(BULB_NAME, false, false, 3);
        bulb.switchOff();
        assertFalse(bulb.isOn());
        assertFalse(bulb.isBroken());
    }

    @Test
    public void switchOffBulb_mustTurnOff() {
        var bulb = new LedBulb(BULB_NAME, true, false, 3);
        bulb.switchOff();
        assertFalse(bulb.isOn());
        assertFalse(bulb.isBroken());
    }

    @Test
    public void switchOffBulbAlreadyBroken_mustNotChange() {
        var bulb = new LedBulb(BULB_NAME, false, true, 0);
        bulb.switchOff();
        assertFalse(bulb.isOn());
        assertTrue(bulb.isBroken());
    }

    @Test
    public void useBulb3TimesWith2RemainingUses_mustBroke() {
        var bulb = new LedBulb(BULB_NAME, false, false, 2);
        bulb.switchOn();
        bulb.switchOff();
        bulb.switchOn();
        bulb.switchOff();
        bulb.switchOn();
    }
}
