package client.models;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client {
        protected String IP;
        protected int port;
        protected Socket clientSocket;
        protected ObjectOutputStream oos;
        protected ObjectInputStream ois;
        protected ArrayList<Course> requestedCourses = new ArrayList<>();
        protected final ArrayList<String> availableSemesters = new ArrayList<>(Arrays.asList("Automne", "Hiver", "Ete"));
        private String semester;

    public Client(String IP, int port) {
            this.IP = IP;
            this.port = port;
        }

        public void run() {
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
        }

        public void disconnect() throws IOException {
            oos.close();
            ois.close();
            clientSocket.close();
        }
        public void requestCourses(String semester) {
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
        }
        public void register2Class(RegistrationForm registration) {
            try{
                clientSocket = new Socket(IP, port);
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ois = new ObjectInputStream(clientSocket.getInputStream());
                sendForm(registration);
                System.out.println(ois.readObject());
                disconnect();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public void sendForm(RegistrationForm registrationForm) throws IOException {
            oos.writeObject("INSCRIRE ");
            oos.flush();
            oos.writeObject(registrationForm);
        }
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
