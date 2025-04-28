package org.example;

import org.example.domain.LedBulb;
import org.example.printer.BulbConsolePrinter;
import org.example.printer.BulbPrinter;

public class Main {

    public static void main(String[] args) {
        BulbPrinter bulbPrinter = new BulbConsolePrinter();

        System.out.println("Creating bulb...\n");
        var bulb = new LedBulb("MyLebBulb");
        bulbPrinter.print(bulb);
        for (int i = 0; i < 3; i++) {
            bulb.switchOn();
            bulbPrinter.print(bulb);
            bulb.switchOff();
            bulbPrinter.print(bulb);
        }
    }
}
