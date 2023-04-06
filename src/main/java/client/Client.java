package client;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
        private String IP;
        private int port;
        private int userInput;
        private String userInfo;
        private Socket clientSocket;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private ArrayList<String> responseTray = new ArrayList<>();
        private ArrayList<Course> requestedCourses = new ArrayList<>();
        private final ArrayList<String> availableSemesters = new ArrayList<>(Arrays.asList("Automne", "Hiver", "Ete"));
        private String semester;
        public Client(String IP, int port) {
            this.IP = IP;
            this.port = port;
        }

        public void run() {
            responseTray.add("*** Bienvenue au portail d'inscription de cours de l'UDEM ***\n");
            emptyResponseTray();
            main();
        }

        public void main() {
            try {
                clientSocket = new Socket(IP, port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ois = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            selectSemester();
        }

        private void emptyResponseTray() {
            while (!responseTray.isEmpty()) {
                        System.out.print(responseTray.get(0));
                        responseTray.remove(0);
            }
        }
        private void selectSemester() {
            responseTray.add("Veuillez choisir la session pour laquelle vous voulez concuslter la liste des cours:\n");
            for (int i = 0; i < availableSemesters.size(); i++) {
                responseTray.add(i+1 + ". " + availableSemesters.get(i) + "\n");
            }
            promptChoice(false);
            semester = availableSemesters.get(userInput-1);
            requestCourses(semester);
        }

        private void promptChoice(Boolean empty) {
            responseTray.add("> Choix: ");
            emptyResponseTray();
            if (!empty) {
                Scanner scan = new Scanner(System.in);
                userInput = scan.nextInt();
            }
        }
        // honnetement stupide, y'a moyen de merge promptChoice et PromptInfo
        private void promptInfo(){
            emptyResponseTray();
            Scanner scan = new Scanner(System.in);
            userInfo = scan.next();
        }

        public void disconnect() throws IOException {
            oos.close();
            ois.close();
            clientSocket.close();
        }
        private void requestCourses(String semester) {
            //emptyResponseTray(); -- necessaire ou pas??
            responseTray.add("Les cours offerts pendant la session d'" + semester.toLowerCase() +" sont: \n");
            try {
                oos.writeObject("CHARGER " + semester);
                oos.flush();
                requestedCourses = (ArrayList<Course>) ois.readObject();
                disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < requestedCourses.size(); i++) {
                responseTray.add(i+1 + ". " + requestedCourses.get(i).getCode() + "\t" + requestedCourses.get(i).getName() + "\n");
            }
            // Sketchy workaround, will fix later
            promptChoice(true);
            selectAction();
        }
        private void selectAction() {
            responseTray.add("\n1. Consulter les cours offerts pour une autre session\n");
            responseTray.add("2. Inscription à un cours\n");
            promptChoice(false);
            if(userInput == 1) {
                run();
            } else if (userInput == 2) {
                register2Class();
            }

        }
        public void register2Class() {
            responseTray.add("Veuillez saisir votre prénom: ");
            promptInfo();
            String firstName = userInfo;
            responseTray.add("Veuillez saisir votre nom: ");
            promptInfo();
            String name = userInfo;
            responseTray.add("Veuillez saisir votre email: ");
            promptInfo();
            String email = userInfo;
            responseTray.add("Veuillez saisir votre matricule: ");
            promptInfo();
            String code = userInfo;
            responseTray.add("Veuillez saisir le code du cours: ");
            promptInfo();
            String courseCode = userInfo;

            RegistrationForm registration = new RegistrationForm(firstName, name,  email, code,  findCourse(courseCode));
            try{
                clientSocket = new Socket(IP, port);
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ois = new ObjectInputStream(clientSocket.getInputStream());
                oos.writeObject("INSCRIRE ");
                oos.flush();
                oos.writeObject(registration);
                disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // absolument horrible et illegal
        public Course findCourse(String code) {
            try {
                int index = -1;

                for (int i = 0; i < requestedCourses.size(); i++) {
                    if (requestedCourses.get(i).getCode().equals(code)) {
                        index = i;
                        break;
                    }
                    ;
                }
                return (requestedCourses.get(index));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

}
