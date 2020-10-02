package Net;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

import static Net.Parser.*;

public class InputReader {
    public static void inputRequest(String[] args) {
        try {
            Parser.parseArgs(args);
            return;
        }
        catch (IllegalArgumentException exc) {
            System.err.println(exc.getMessage());
            System.out.println("Please, enter file path (file must be not empty), " +
                    "server IP address or name and server port.");
        }

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        for (int it = 0; it < 5; ++it) {
            try {
                path = FileWorker.getFilePath(scanner.next());
                serverAddress = InetAddress.getByName(scanner.next());
                port = scanner.nextInt();
                if (port < 0 || port > 65535 || path == null || Files.size(path) > 8.8e12) {
                    throw new IllegalArgumentException("Bad arguments or file problems.");
                }
                System.out.println("Success. File path is: " + path + ", server address: " + serverAddress +
                        ", server port is: " + port + ".");
                return;
            }
            catch(IOException | IllegalArgumentException exc) {
                if (it == 4) {
                    System.out.println("Session closed. Try again later.");
                    System.exit(-1);
                }
                else System.out.println(exc.getMessage() + " Unable to identify path, address/name, or port. " +
                        "You have " + (4 - it) + " tries remain.");
            }
        }
    }
}
