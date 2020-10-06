package Net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Server {
    public static int port;
    public static int clientsNumber = 0;
    private static ServerSocket serverSocket;
    private static InetAddress serverAddress;
    private static final ArrayList<Socket> clients = new ArrayList<>();

    public static void start() throws IOException {
        System.out.print("Server is up on port " + port + ".");

        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> {
                    try {
                        closeClients();
                        if (!serverSocket.isClosed()) {
                            serverSocket.close();
                        }
                        System.out.println("Server is down.");
                    }
                    catch (IOException exc) {
                        System.err.println(exc.getMessage());
                    }
                }
        ));

        try {
            //serverAddress = InetAddress.getByName("127.0.0.1");
            serverAddress = InetAddress.getLocalHost();
            serverSocket = new ServerSocket(port, 77, serverAddress);
            System.out.println(" IP: " + serverAddress);

            Path uploads = Paths.get("uploads");
            if (!Files.exists(uploads)) {
                Files.createDirectory(uploads);
            }

            while (!serverSocket.isClosed()) {
                if (clients.size() % 10 == 0) {
                    clients.removeIf(Socket::isClosed);
                }
                Socket clientSocket = serverSocket.accept();
                ++clientsNumber;
                clients.add(clientSocket);
                Thread client = new Thread(new ClientRoutine(clientSocket, clientsNumber));
                System.out.println("Client #" + clientsNumber + " is connected.");
                client.start();
            }
        }
        finally {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
            closeClients();
        }
    }

    private static void closeClients() throws IOException {
        for (Socket client : clients) {
            if (!client.isClosed()) {
                client.close();
            }
        }
    }
}