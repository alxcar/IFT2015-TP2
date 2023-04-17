package client;

/**
 * Classe du client dans le terminal
 */
public class client_simple {
    public static void main(String[] args) {
        ClientTerminal client = new ClientTerminal("127.0.0.1", 1337);
        client.run();
    }
}
