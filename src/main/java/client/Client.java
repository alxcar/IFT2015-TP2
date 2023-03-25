package client;

import server.Server;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
        private String IP;
        private int port;
        private int userInput;
        private Socket clientSocket;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private ArrayList<String> responseTray = new ArrayList<>();
        private ArrayList<Course> requestedCourses = new ArrayList<>();
        // Ideally we would request those from the server...
        private final ArrayList<String> registrationQuestions = new ArrayList<>(Arrays.asList("Veuillez saisir votre prénom: ",
                "Veuillez saisir votre nom: ", "Veuillez saisir votre email: ", "Veuillez saisir votre matricule: ",
                "Veuillez saisir le code du cours: "));
        private final ArrayList<String> availableSemesters = new ArrayList<>(Arrays.asList("Automne", "Hiver", "Ete"));
        private String semester;
        public Client(String IP, int port) {
            this.IP = IP;
            this.port = port;
        }
        public void reqServer(String IP, int port) {
            try {
                if(oos != null) {
                    oos.close();
                    ois.close();
                }
                clientSocket = new Socket(IP, port);
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ois = new ObjectInputStream(clientSocket.getInputStream());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public void run() {
            responseTray.add("*** Bienvenue au portail d'inscription de cours de l'UDEM ***\n");
            emptyResponseTray();
            semesterPrompt();
        }

        private void emptyResponseTray() {
            while (!responseTray.isEmpty()) {
                        System.out.print(responseTray.get(0));
                        responseTray.remove(0);
            }
        }
        private void semesterPrompt() {
            selectSemester();
            requestCourses(semester);
            emptyResponseTray();
            // Sketchy workaround, will fix later
            promptChoice(true);
            selectAction();
        }
        private void selectSemester() {
            responseTray.add("Veuillez choisir la session pour laquelle vous voulez concuslter la liste des cours:\n");
            for (int i = 0; i < availableSemesters.size(); i++) {
                responseTray.add(i+1 + ". " + availableSemesters.get(i) + "\n");
            }
            promptChoice(false);
            semester = availableSemesters.get(userInput);
        }

        private void promptChoice(Boolean empty) {
            responseTray.add("> Choix: ");
            emptyResponseTray();
            if (!empty) {
                Scanner scan = new Scanner(System.in);
                userInput = scan.nextInt() - 1;
            }
        }

        private void requestCourses(String semester) {
            reqServer(IP, port);
            responseTray.add("Les cours offerts pendant la session d'" + semester.toLowerCase() +" sont: \n");
            try {
                oos.writeObject("CHARGER " + semester);
                requestedCourses = (ArrayList<Course>) ois.readObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < requestedCourses.size(); i++) {
                responseTray.add(i+1 + ". " + requestedCourses.get(i).getCode() + "\t" + requestedCourses.get(i).getName() + "\n");
            }
        }
        private void selectAction() {
            responseTray.add("\n1. Consulter les cours offerts pour une autre session\n");
            responseTray.add("2. Inscription à un cours\n");
            promptChoice(false);
            if(userInput+1 == 1) {
                selectSemester();
            } else if (userInput+1 == 2) {
                register2Class();
            }

        }
        public void register2Class() {
            reqServer(IP, port);
            // TODO: Make this.. good? better?
            ArrayList<String> temp = new ArrayList<>();
            Scanner scan = new Scanner(System.in);
            for (int i = 0; i < registrationQuestions.size(); i++) {
                responseTray.add(registrationQuestions.get(i));
                emptyResponseTray();
                temp.add(scan.next());
            }
            for (int i = 0; i < requestedCourses.size(); i++) {
                if (temp.get(4).equals(requestedCourses.get(i).getCode())) {
                    try {
                        oos.writeObject("INSCRIRE");
                        //oos.writeObject(new RegistrationForm(temp.get(0),temp.get(1),temp.get(2),temp.get(3), requestedCourses.get(i)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }

}
