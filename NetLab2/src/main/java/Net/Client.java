package Net;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;

public class Client {
    private static final Socket clientSocket = new Socket();
    private static DataOutputStream socketOutputStream;
    private static BufferedReader socketReader;
    private static final byte[] sendBuffer = new byte[512];

    public static void start() throws IOException {
        try (InputStream inputStream = FileWorker.getInputStream(Parser.path)) {

            if (inputStream == null) {
                System.err.println("File problems detected.");
                return;
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (!clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                    socketOutputStream.close();
                    socketReader.close();
                    System.out.println("Terminated.");
                }
                catch (IOException exc) {
                    System.err.println("Terminated with exception: " + exc.getMessage());
                }
            }));

            clientSocket.connect(new InetSocketAddress(Parser.serverAddress, Parser.port));
            System.out.println("Connected.");
            socketOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            long fileSize = Files.size(Parser.path);
            socketOutputStream.writeUTF(Parser.path.getFileName().toString());
            socketOutputStream.writeLong(fileSize);
            socketOutputStream.flush();
            
            int tmp = 0;
            while (true) {
                tmp = inputStream.read(sendBuffer, 0, 512);
                if (tmp >= 0) {
                    socketOutputStream.write(sendBuffer, 0, tmp);
                }
                else {
                    break;
                }
            }
            socketOutputStream.flush(); //flush needs here to avoid dead lock

            String reply = socketReader.readLine();
            switch (reply) {
                case "Success" -> System.out.println("File was successfully uploaded by the server.");
                case "Failure" -> System.out.println("Some errors occurred while the server was uploading the file.");
            }
        } finally {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }
            socketOutputStream.close();
            socketReader.close();
        }
    }
}
 