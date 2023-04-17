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

/**
 * Classe Controller, gère les interactions entre le GUI et le serveur.
 */
public class Controller {
    private View view;

    /**
     * Constructeur de la classe Controller
     * @param view GUI géné avec lequel le controller interagie.
     */
    public Controller(View view) {
        this.view = view;
    }

    /**
     * Créer une connexion au serveur et demande a liste de cours disponible pour la session désirée. Affiche la liste
     * de cours dans la table prévue à cette effet dans le GUI.
     */
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

    /**
     * S'assure que les informations fournis par le user sont valide puis créer un RegistrationForm.
     * Se connect au serveur et envoie la demande d'inscription.
     * @param prenom Prenom fournis par le user
     * @param nom Nom fournis par le user
     * @param email email fournis par le user, finissant par @umontreal.ca.
     * @param matricule matricule à 8 chiffre fournis par le user.
     */
    public void sendForm(String prenom, String nom, String email, String matricule) {
        ArrayList<String> error = new ArrayList<>();
        view.removeBorder(view.table);
        if (view.table.getSelectionModel().getSelectedItem() == null) {
            view.setRedBorder(view.table);
            error.add("Vous devez selectionner un cours!");
            view.throwErrorAlert(error);
        } else {
            view.removeBorder(view.getEmail());
            if (!verifyEmail(email)) {
                view.setRedBorder(view.getEmail());
                error.add("Le champ \"Email\" est invalide!");
            }
            view.removeBorder(view.getMatricule());
            if (!verifyMatricule(matricule)) {
                view.setRedBorder(view.getMatricule());
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
