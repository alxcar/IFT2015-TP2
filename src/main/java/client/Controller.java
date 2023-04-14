package client;

import client.models.Client;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
        ArrayList<String> error = new ArrayList<>();
        view.removeBorder(view.table);
        if (view.table.getSelectionModel().getSelectedItem() == null) {
            view.setRedBorder(view.table);
            error.add("Vous devez selectionner un cours!");
            view.throwErrorAlert(error);
        } else {
            view.removeBorder(view.email);
            if (!verifyEmail(email)) {
                view.setRedBorder(view.email);
                error.add("Le champ \"Email\" est invalide!");
            }
            view.removeBorder(view.matricule);
            if (!verifyMatricule(matricule)) {
                view.setRedBorder(view.matricule);
                error.add("Le champ \"Matricule\" est invalide!");
            }
            if (!error.isEmpty()){
                view.throwErrorAlert(error);
            } else {
                Client client = new Client("127.0.0.1", 1337);
                client.run();
                Course selectedCourse = view.table.getSelectionModel().getSelectedItem();
                RegistrationForm newForm = new RegistrationForm(prenom, nom, email, matricule, selectedCourse);
                view.clearForm();
                view.successAlert(nom, prenom, selectedCourse.getCode());
                try {
                    client.sendForm(newForm);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }

    private boolean verifyEmail(String email) {
        Pattern p = Pattern.compile("(.+?)"+"@umontreal.ca");
        Matcher mat = p.matcher(email);
        return mat.matches();
    }
    private boolean verifyMatricule(String matricule){
        Pattern p = Pattern.compile("^[0-9]{8}$");
        Matcher mat = p.matcher(matricule);
        return mat.matches();
    }
}
