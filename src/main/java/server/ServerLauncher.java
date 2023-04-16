package server;

/**
 * Simple classe pour lancé le serveur
 */
public class ServerLauncher {
    /**
     * Port que le serveur écoutera
     */
    public final static int PORT = 1337;
    /**
     * Créer un nouvel object Serveur et le démarre
     */
    public static void main(String[] args) {
        ClientRequest server;
        try {
            server = new ClientRequest(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}