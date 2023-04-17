package client.models;


import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;

import java.util.ArrayList;

import java.util.Scanner;

public class ClientTerminal extends Client {
        private int userInput;
        private String userInfo;
        private ArrayList<String> responseTray = new ArrayList<>();
        private String semester;
        public ClientTerminal(String IP, int port) {
            super(IP, port);
            responseTray.add("*** Bienvenue au portail d'inscription de cours de l'UDEM ***\n");
            emptyResponseTray();
        }
        @Override
        public void run() {
            super.run();
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

        @Override
        public void requestCourses(String semester) {
            super.requestCourses(semester);
            responseTray.add("Les cours offerts pendant la session d'" + semester.toLowerCase() +" sont: \n");
            for (int i = 0; i < requestedCourses.size(); i++) {
                responseTray.add(i+1 + ". " + requestedCourses.get(i).getCode() + "\t" + requestedCourses.get(i).getName() + "\n");
            }
            emptyResponseTray();
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
            super.register2Class(registration);
        }

        public void sendForm(RegistrationForm registrationForm) throws IOException {
            oos.writeObject("INSCRIRE ");
            oos.flush();
            oos.writeObject(registrationForm);
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
                throw new RuntimeException("le cours exist pas dumbass");
            }

        }

        public ArrayList<Course> getCourses() {
            return requestedCourses;
        }

}
