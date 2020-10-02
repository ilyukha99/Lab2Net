package Net;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import static java.nio.file.StandardOpenOption.*;

public class FileWorker {

    public static BufferedReader getReader(Path path) throws IOException {
        if (!Files.isDirectory(path) && Files.exists(path) && Files.isReadable(path) && Files.size(path) > 0) {
            return new BufferedReader(new FileReader(path.toString()), 4200);
        }
        return null;
    }

    public static Path getFilePath(String somePath) {
        Path path = Paths.get(somePath);
        if (!Files.isDirectory(path)) {
            return path;
        }
        return null;
    }

    //may delete files with data while using
    public static BufferedWriter getWriter(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            Files.deleteIfExists(path);
            return Files.newBufferedWriter(path, CREATE, WRITE);
        }
        return null;
    }
}
