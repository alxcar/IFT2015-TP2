package client;

import client.models.Client;
import javafx.stage.Stage;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    private View view;

    public Controller(View view) {
        this.view = view;
    }

    public void chargerCours() {
        view.table.getItems().clear();
        Client client = new Client("127.0.0.1", 1337);
        client.run();
        client.requestCourses((String) view.sessionBox.getValue());
        try {
            client.disconnect();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        ArrayList<Course> requestedCourses = new ArrayList<>();
        requestedCourses = client.getCourses();
        int i = 0;
        while (requestedCourses.size() > i) {
            view.table.getItems().add(requestedCourses.get(i));
            i++;
        }
    }

    public void sendForm(String prenom, String nom, String email, String matricule) {
        try {
            if (!verifyMatricule(matricule)) {
                throw new NumberFormatException();
            } else if (!verifyEmail(email)) {
                System.out.println("Email invalide: veuillez entrer un email @umontreal.ca");
            } else {
                Client client = new Client("127.0.0.1", 1337);
                client.run();
                Course selectedCourse = view.table.getSelectionModel().getSelectedItem();
                RegistrationForm newForm = new RegistrationForm(prenom, nom, email, matricule, selectedCourse);
                try {
                    client.sendForm(newForm);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } catch (NumberFormatException ex) {
            System.out.println("Matricule invalide: Veuillez entrer un matricule valide à 8 chiffres");
        }
    }

    private boolean verifyEmail(String email) {
        Pattern p = Pattern.compile("(.+?)"+"@umontreal.ca");
        Matcher mat = p.matcher(email);
        return mat.matches();
    }
    private boolean verifyMatricule(String matricule){
        Pattern p = Pattern.compile("\\d*8");
        Matcher mat = p.matcher(matricule);
        return mat.matches();
    }
}
