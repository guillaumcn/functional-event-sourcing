package org.example.printer;

import org.example.domain.Bulb;

public class BulbConsolePrinter implements BulbPrinter {

    @Override
    public void print(Bulb bulb) {
        var statusText = bulb.isOn() ? "ON" : "OFF";
        var brokenText = bulb.isBroken() ? "YES" : "NO";
        System.out.printf("Bulb %s\nStatus : %s\nBroken : %s\n\n", bulb.getName(), statusText, brokenText);
    }
}
