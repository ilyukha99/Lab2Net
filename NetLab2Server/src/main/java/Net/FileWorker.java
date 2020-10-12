package Net;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import static java.nio.file.StandardOpenOption.*;

public class FileWorker {

    public static Path getFilePath(String somePath) {
        Path path = Paths.get(somePath);
        if (!Files.isDirectory(path)) {
            return path;
        }
        return null;
    }

    //may delete files with data while using
    public static OutputStream getOutputStream(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            Files.deleteIfExists(path);
            return Files.newOutputStream(path, CREATE, WRITE);
        }
        return null;
    }
}
