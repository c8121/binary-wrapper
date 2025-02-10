package de.c8121.binarywrapper.testutil;

import javax.swing.*;
import java.io.Console;
import java.util.Scanner;

public class TestCli {

    private static Scanner scanner = null;

    /**
     *
     */
    public static String ask(final String message, final String defaultAnswer) {

        if (scanner == null)
            scanner = new Scanner(System.in);

        System.out.print(message + (defaultAnswer != null ? " (Empty=" + defaultAnswer + ")" : "") + ": ");
        String result = scanner.nextLine();
        System.out.println();

        return result.isBlank() ? defaultAnswer : result;
    }

    /**
     *
     */
    public static String askPassword(final String message) {

        Console console = System.console();
        if (console == null) {
            return getPasswordWithoutConsole(message);
        } else {
            return String.valueOf(console.readPassword(message));
        }
    }

    /**
     *
     */
    private static String getPasswordWithoutConsole(final String message) {
        final JPasswordField passwordField = new JPasswordField();
        return JOptionPane.showConfirmDialog(
                null,
                passwordField,
                message,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION
                ? new String(passwordField.getPassword())
                : "";
    }
}
