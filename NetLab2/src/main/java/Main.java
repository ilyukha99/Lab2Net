import Net.Client;
import Net.InputReader;

public class Main {
    public static void main(String[] args) {
        try {
            InputReader.inputRequest(args);
            Client.start();
        }
        catch (Exception exc) {
            System.err.println(exc.getMessage());
        }
    }
}
