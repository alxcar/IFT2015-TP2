package server;

public class ServerLauncher {
    public final static int PORT = 1337;

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