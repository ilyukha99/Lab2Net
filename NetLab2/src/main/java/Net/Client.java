package Net;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;

public class Client {
    private static final Socket clientSocket = new Socket();
    private static BufferedWriter socketWriter;
    private static BufferedReader socketReader;
    private static final char[] sendBuffer = new char[4200];

    public static void start() throws IOException {
        try (BufferedReader fileReader = FileWorker.getReader(Parser.path)) {

            if (fileReader == null) {
                System.err.println("File problems detected.");
                return;
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (!clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                    socketWriter.close();
                    socketReader.close();
                    System.out.println("Terminated.");
                }
                catch (IOException exc) {
                    System.err.println("Terminated with exception: " + exc.getMessage());
                }
            }));

            clientSocket.connect(new InetSocketAddress(Parser.serverAddress, Parser.port));
            System.out.println("Connected.");
            socketWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            long fileSize = Files.size(Parser.path);
            socketWriter.write(Parser.path.getFileName().toString() + "\n");
            socketWriter.write(fileSize + "\n");
            socketWriter.flush();
            
            int tmp = 0;
            while (true) {
                tmp = fileReader.read(sendBuffer, 0, 4096);
                if (tmp >= 0) {
                    socketWriter.write(sendBuffer, 0, tmp);
                }
                else {
                    break;
                }
            }
            socketWriter.flush(); //flush needs here to avoid dead lock

            String reply = socketReader.readLine();
            switch (reply) {
                case "Success" -> System.out.println("File was successfully uploaded by the server.");
                case "Failure" -> System.out.println("Some errors occurred while the server was uploading the file.");
            }
        } finally {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }
            socketWriter.close();
            socketReader.close();
        }
    }
}
 