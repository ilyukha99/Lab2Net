package Net;

import java.io.IOException;
import java.nio.file.Path;
import java.net.InetAddress;

//gets file path, server ip/dns name, server port
public class Parser {
    static Path path;
    static InetAddress serverAddress;
    static int port;

    public static void parseArgs(String[] args) throws IllegalArgumentException {
        if (args.length < 3) {
            throw new IllegalArgumentException("Too few arguments found.");
        }

        try {
            path = FileWorker.getFilePath(args[0]);
            serverAddress = InetAddress.getByName(args[1]);
            port = Integer.parseInt(args[2]);
        }
        catch(IOException exc) {
            throw new IllegalArgumentException("Bad arguments.");
        }

        if (path == null || port < 0 || port > 65535) {
            throw new IllegalArgumentException("Bad arguments.");
        }

        System.out.println("Success. File path is: " + path + ", server address: " + serverAddress +
                ", server port is: " + port + ".");
    }
}
