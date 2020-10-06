package Net;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static Net.Server.port;

public class InputReader {

    public static void inputRequest(String[] args) {
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port > 0 && port < 65536) {
                    return;
                }
            }
            catch(NumberFormatException ignored) {}
        }

        System.out.println("Please, enter the number of port for server to listen from.");
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        for (int it = 0; it < 5 && scanner.hasNextInt(); ++it) {
            try {
                port = scanner.nextInt();
                if (port > 0 && port < 65536) {
                    return;
                } else throw new IllegalArgumentException("Bad port number.");
            }

            catch (IllegalArgumentException exc) {
                System.out.println(exc.getMessage() + " Try again. Port must be more than 0 and not more than 65535."
                        + " You have " + (4 - it) + " tries remain.");
            }
        }
    }
}
