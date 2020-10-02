package Net;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientRoutine implements Runnable {
    private final Socket clientSocket;
    private BufferedReader socketReader;
    private BufferedWriter socketWriter;
    private final char[] buffer = new char[4200];
    private Path filePath;
    private final int clientNumber;

    public ClientRoutine(Socket clientSocket, int number) {
        this.clientSocket = clientSocket;
        clientNumber = number;
    }

    @Override
    public void run() {
        try {
            socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socketWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String fileName = socketReader.readLine();
            final long fileSize = Long.parseLong(socketReader.readLine());
            filePath = FileWorker.getFilePath("uploads\\" + fileName);
            BufferedWriter fileWriter = FileWorker.getWriter(filePath);
            if (filePath == null || fileWriter == null) {
                socketWriter.write("Failure");
                return;
            }

            int tmp;
            boolean flag = false;
            long bytes = 0, initialTime = System.nanoTime(), lastTime = initialTime, lastBytesAmount = bytes;
            while (bytes < fileSize) {
                tmp = socketReader.read(buffer, 0, 4096);

                if (tmp >= 0) {
                    fileWriter.write(buffer, 0, tmp);
                }
                bytes += tmp;

                if (System.currentTimeMillis() - lastTime > 3000) {
                    println("#" + clientNumber + ": instant speed = " + (bytes - lastBytesAmount) * 8 / 3000 + " Kbps, "
                      + "average speed = " + bytes * 8_000_000 / ((System.nanoTime() - initialTime)) + " Kbps");
                    lastTime = System.currentTimeMillis();
                    lastBytesAmount = bytes;
                    flag = true;
                }
            }
            if (!flag) {
                println("#" + clientNumber + ": instant speed = " + bytes * 8_000_000 /
                        (System.nanoTime() - initialTime) + " Kbps");
            }
            fileWriter.flush();

            String status;
            if (Files.size(filePath) != fileSize) {
                status = "Failure";
            }
            else {
                status = "Success";
            }
            socketWriter.write(status);
            socketWriter.flush(); //to avoid dead lock

            println("Client #" + clientNumber + ", IP: " + clientSocket.getInetAddress() +
                    " terminated with " + status + " status.");
        }

        catch (IOException | ArithmeticException exc) {
            println("Client #" + clientNumber + ", IP: " + clientSocket.getInetAddress() +
                "was terminated with exception: " + exc.getMessage());
        }

        finally {
            try {
                socketReader.close();
                socketWriter.close();
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            }
            catch (IOException exc) {
                System.err.println(exc.getMessage());
            }
        }
    }

    public synchronized void println(String str) {
        System.out.println(str);
    }
}
