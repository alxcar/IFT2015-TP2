package server;

import java.io.IOException;

public class localServer {
    public static void main(String[] args) {
        try {
            Server server = new Server(1337);
            System.out.println("Serveur lancé!");
            server.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
