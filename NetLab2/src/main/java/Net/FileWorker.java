package Net;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.*;

public class FileWorker {
    public static InputStream getInputStream(Path path) throws IOException {
        if (!Files.isDirectory(path) && Files.exists(path) && Files.isReadable(path)) {
            return Files.newInputStream(path, READ);
        }
        return null;
    }

    public static Path getFilePath(String somePath) throws IOException {
        Path path = Paths.get(somePath);
        if (!Files.isDirectory(path) || Files.size(path) <= 4096) {
            return path;
        }
        return null;
    }
}
