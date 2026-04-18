package com.bms.util;

import java.io.Console;
import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleHelper {
    private final Scanner scanner;
    private final Console console;

    public ConsoleHelper() {
        this.scanner = new Scanner(System.in);
        this.console = System.console();
    }

    public String readRequiredLine(String prompt) {
        while (true) {
            String input = readLine(prompt).trim();
            if (!input.isBlank()) {
                return input;
            }
            System.out.println("This field is required.");
        }
    }

    public int readInt(String prompt) {
        while (true) {
            String input = readLine(prompt).trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    public BigDecimal readAmount(String prompt) {
        while (true) {
            String input = readLine(prompt).trim();
            try {
                BigDecimal amount = new BigDecimal(input);
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    return amount;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Enter a valid amount greater than zero.");
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String input = readLine(prompt).trim().toLowerCase();
            if ("y".equals(input) || "yes".equals(input)) {
                return true;
            }
            if ("n".equals(input) || "no".equals(input)) {
                return false;
            }
            System.out.println("Please enter y or n.");
        }
    }

    public String readSecret(String prompt) {
        if (console != null) {
            char[] chars = console.readPassword("%s", prompt);
            return new String(chars);
        }
        return readRequiredLine(prompt);
    }

    public String readPin(String prompt) {
        while (true) {
            String pin = readSecret(prompt).trim();
            if (pin.matches("\\d{4}")) {
                return pin;
            }
            System.out.println("PIN must be exactly 4 digits.");
        }
    }

    private String readLine(String prompt) {
        if (console != null) {
            return console.readLine("%s", prompt);
        }

        System.out.print(prompt);
        return scanner.nextLine();
    }
}
