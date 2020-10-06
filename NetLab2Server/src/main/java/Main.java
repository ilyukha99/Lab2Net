import Net.InputReader;
import Net.Server;

public class Main {
    public static void main(String[] args) {
        try {
            InputReader.inputRequest(args);
            Server.start();
        }
        catch (Exception exc) {
            System.err.println(exc.getMessage());
        }
    }
}
