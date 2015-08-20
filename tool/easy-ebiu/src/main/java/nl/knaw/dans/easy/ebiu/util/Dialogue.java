package nl.knaw.dans.easy.ebiu.util;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import nl.knaw.dans.easy.ebiu.exceptions.FatalRuntimeException;

public class Dialogue {

    public static boolean confirm(String question) {
        System.out.print(question + " [y][n] ");
        String answer = readInput();
        boolean confirmed = answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
        return confirmed;
    }

    public static String getInput(String prompt) {
        System.out.print(prompt + " ");
        String answer = readInput();
        return answer;
    }

    private static String readInput() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;
        try {
            input = br.readLine();
        }
        catch (IOException e) {
            throw new FatalRuntimeException("Cannot read user input", e);
        }

        return input;
    }

    public static String readPass(String prompt) {
        Console c = System.console();
        if (c == null) {
            System.out.println("Warning! Input for password not concealed.");
            return getInput(prompt);
        } else {
            char[] pass = c.readPassword(prompt + " ");
            return String.valueOf(pass);
        }

    }

}
