package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe ClientRequest. Elle accepte les connexions de client et les envoies au Serveur sur un autre Thread.
 */
public class ClientRequest {
    /**
     * "Socket" du serveur.
     */
    private final ServerSocket server;
    /**
     * "Socket" du client.
     */
    private Socket client;
    /**
     * ObjectInputStream entre Client -> Serveur
     */
    private ObjectInputStream objectInputStream;
    /**
     * ObjectInputStream entre Client <- Serveur
     */
    private ObjectOutputStream objectOutputStream;
    /**
     * Constructeur de la classe ClientRequest.
     * @param port Le port utilisé
     * @throws IOException Exception lancé lors d'erreur reliée à l'ouverture du Socket.
     */
    public ClientRequest(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
    }
    /**
     * Lance le serveur et instancie les objets necessaire à l'envoie et la reception de données du client. Créer une
     * instance de Serveur sur un nouveau thread, puis démarre ce thread.
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());

                Thread serverRequest = new Server(client, objectOutputStream, objectInputStream);
                serverRequest.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
