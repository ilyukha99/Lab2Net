package Net;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientRoutine implements Runnable {
    private final Socket clientSocket;
    private DataInputStream dataInputStream;
    private BufferedWriter socketWriter;
    private final byte[] buffer = new byte[512];
    private Path filePath;
    private final int clientNumber;

    public ClientRoutine(Socket clientSocket, int number) {
        this.clientSocket = clientSocket;
        clientNumber = number;
    }

    @Override
    public void run() {
        try {
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            socketWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String fileName = dataInputStream.readUTF();
            final long fileSize = Long.parseLong(dataInputStream.readUTF());
            filePath = FileWorker.getFilePath("uploads\\" + fileName);
            OutputStream fileOutputStream = FileWorker.getOutputStream(filePath);
            if (filePath == null || fileOutputStream == null) {
                socketWriter.write("Failure");
                return;
            }

            int tmp;
            boolean flag = false;
            long bytes = 0, initialNanoTime = System.nanoTime(), lastBytesAmount = 0,
                    initialTime = System.currentTimeMillis(), lastTime = initialTime;
            while (bytes < fileSize) {
                tmp = dataInputStream.read(buffer, 0, 512);

                if (tmp >= 0) {
                    fileOutputStream.write(buffer, 0, tmp);
                }
                bytes += tmp;

                if (System.currentTimeMillis() - lastTime > 3000) {
                    println("#" + clientNumber + ": instant speed = " + (bytes - lastBytesAmount) * 8 / 3000 + " Kbps, "
                      + "average speed = " + bytes * 8 / ((System.currentTimeMillis() - initialTime)) + " Kbps");
                    lastTime = System.currentTimeMillis();
                    lastBytesAmount = bytes;
                    flag = true;
                }
            }
            if (!flag) {
                println("#" + clientNumber + ": instant speed = " + bytes * 8_000_000 /
                        (System.nanoTime() - initialNanoTime) + " Kbps");
            }
            fileOutputStream.flush();

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
                " was terminated with exception: " + exc.getMessage());
        }

        finally {
            try {
                dataInputStream.close();
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
