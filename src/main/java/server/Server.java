package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {

    /**
     * Le client souhaite s'inscrire à un cours.
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * Le client souhaite charger la liste de cours disponible.
     */
    public final static String LOAD_COMMAND = "CHARGER";
    /**
     * "Socket" du serveur.
     */
    private final ServerSocket server;
    /**
     * "Socket" du client.
     */
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Constructeur de la classe Serveur.
     * @param port Port auquel le serveur écoutera.
     * @throws IOException
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     @throws Exception si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux
     */
    public void handleLoadCourses(String arg) {
        try {
            FileReader coursesTxt = new FileReader("src/main/java/server/data/cours.txt");
            BufferedReader reader = new BufferedReader(coursesTxt);
            ArrayList<Course> requestedCourses = new ArrayList<>();
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] tempCourse = currentLine.split("\t");
                if (tempCourse[2].equals(arg)) {
                    requestedCourses.add(new Course(tempCourse[0], tempCourse[1], tempCourse[2]));
                    System.out.println(requestedCourses);
                }
            }
            objectOutputStream.writeObject(requestedCourses);
            objectOutputStream.flush();
            reader.close();
        }  catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     @throws Exception si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        try {
            RegistrationForm registration = (RegistrationForm) objectInputStream.readObject();

            FileWriter fw = new FileWriter("data/inscription.txt");
            BufferedWriter writer = new BufferedWriter(fw);
            Course cours = registration.getCourse();
            writer.append(cours.getSession() + "\t" + cours.getCode() + "\t" + registration.getMatricule() + "\t" +
                    registration.getPrenom() + "\t" + registration.getNom() + "\t" + registration.getEmail());
        } catch (ClassNotFoundException ex) {

        } catch (IOException ex) {

        }

    }
}

