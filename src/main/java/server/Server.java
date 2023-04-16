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

/**
 * Classe Serveur. Elle accepte et gère une requête du client, communiqué sous forme de commande.
 */
public class Server extends Thread {

    /**
     * Le client souhaite s'inscrire à un cours.
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * Le client souhaite charger la liste de cours disponible.
     */
    public final static String LOAD_COMMAND = "CHARGER";
    /**
     * "Socket" du client.
     */
    private Socket client;
    /**
     * Liste des handlers
     */
    private final ArrayList<EventHandler> handlers;
    /**
     * ObjectInputStream entre Client -> Serveur
     */
    private ObjectInputStream objectInputStream;
    /**
     * ObjectInputStream entre Client <- Serveur
     */
    private ObjectOutputStream objectOutputStream;

    /**
     * Constructeur de la classe Serveur.
     * @throws IOException Exception lancé le port est deja en utilisation ??? IDK ???
     * @param client Socket du client ayant fait la demande
     * @param objectInputStream InputStream entre le client et le serveur
     * @param objectOutputStream OutputStream entre le client et le serveur
     */
    public Server(Socket client, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException {
        this.client = client;
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
    }

    /**
     * Override de le methode run() de Thread, elle est lancée sur un thread différent lorsque Object.start() est
     * appelée. Elle appelle la méthode listen() pour "handle" la commande reçu, puis ferme la connection avec le
     * client.
     */
    public void run() {
        try {
            listen();
            disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Ajoute chaque EventHandler à l'ArrayList handlers
     * @param h L'evenement à traiter
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Reçois la commande du client et separe la commande de ses arguments.
     * @throws IOException Exception lancé si objectInputStream n'est pas instancié
     * @throws ClassNotFoundException Exception lancé si l'objet reçu n'existe pas ou n'est pas reconnu.
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Créer une pair entre la commande et ses arguments. La commande représente la Key et les arguments sont les Values
     * @param line Ligne de commande à traiter reçu du client.
     * @return une pair entre la commande et les arguments. Les arguments sont accessible par la Key
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Ferme la connexion précedement ouverte avec le client
     * @throws IOException Lancé si les objects n'ont pas été instancié, donc qu'aucune connexion n'est établie
     * avec le client.
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
        System.out.println("Client déconnecté!");
    }

    /**
     * Appelle la methode approprié selon la commande reçu du client.
     * @param cmd Type de commande reçu par le client. Détermine quelle methode est appellé.
     * @param arg Arguments fournis par le client. Ils sont envoyés envoyé à la methode determiné par cmd
     */
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
     */
    public void handleLoadCourses(String arg){
        try {
            FileReader coursesTxt = new FileReader("src/main/java/server/data/cours.txt");
            BufferedReader reader = new BufferedReader(coursesTxt);
            ArrayList<Course> requestedCourses = new ArrayList<>();
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] tempCourse = currentLine.split("\t");
                if (tempCourse[2].equals(arg)) {
                    requestedCourses.add(new Course(tempCourse[1], tempCourse[0], tempCourse[2]));
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
     */
    public void handleRegistration() {
        try {
            RegistrationForm registration = (RegistrationForm) objectInputStream.readObject();
            FileWriter fw = new FileWriter("src/main/java/server/data/inscription.txt", true);
            BufferedWriter writer = new BufferedWriter(fw);
            Course cours = registration.getCourse();
            writer.append(cours.getSession() + "\t" + cours.getCode() + "\t" + registration.getMatricule() + "\t" +
                    registration.getPrenom() + "\t" + registration.getNom() + "\t" + registration.getEmail() + "\n");
            writer.close();
            objectOutputStream.writeObject("Félicitations " + registration.getPrenom() + ", l'inscription au cours "
                                            + cours.getCode() + " est réussite.");
            objectOutputStream.flush();
        } catch (ClassNotFoundException ex) {

        } catch (IOException ex) {

        }

    }
}

