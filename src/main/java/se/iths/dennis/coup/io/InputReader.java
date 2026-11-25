package se.iths.dennis.coup.io;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputReader {
    Scanner sc = new Scanner(System.in);

    public String readNextLine() {
        return sc.nextLine();
    }

    public int readNextInt() {
        while (true) {
            try {
                int number = sc.nextInt();
                sc.nextLine();
                return number;

            } catch (InputMismatchException e) {
                System.out.println("Wrong, you have to type a number.");
                sc.nextLine();
            }
        }
    }
}
