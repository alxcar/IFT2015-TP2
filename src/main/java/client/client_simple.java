package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class client_simple {
    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 1337);
        client.run();
    }
}
