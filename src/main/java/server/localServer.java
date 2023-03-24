package server;

import java.io.IOException;

public class localServer {
    public static void main(String[] args) {
        try {
            Server server = new Server(1337);
            server.run();
            System.out.println("Serveur lancé!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
