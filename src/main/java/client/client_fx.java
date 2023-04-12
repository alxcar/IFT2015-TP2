package client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import server.models.Course;
import server.models.RegistrationForm;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class client_fx extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        int width = 750;
        int height = 500;
        int padding = 20;
        HBox mainPlane = new HBox();
        VBox leftPlane = new VBox();
        leftPlane.setPadding(new Insets(padding, padding, padding, padding));
        leftPlane.setPrefSize(width/2, height);
        leftPlane.setAlignment(Pos.CENTER);
        VBox rightPlane = new VBox();
        rightPlane.setPadding(new Insets(padding, padding, padding, padding));
        rightPlane.setPrefSize(width/2, height);
        rightPlane.setAlignment(Pos.CENTER);

        Label listeLabel = new Label("Liste des cours");
        listeLabel.setFont(new Font(25));

        TableView<Course> table = new TableView();
        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Course, String> coursCol = new TableColumn("Cours");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.getColumns().addAll(codeCol, coursCol);
        // Faire fit les columns sur la longueur du tab
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        HBox chargerPlane = new HBox();
        chargerPlane.setSpacing(padding*3);
        chargerPlane.setAlignment(Pos.CENTER);
        ChoiceBox sessionBox = new ChoiceBox();
        sessionBox.getItems().add("Automne");
        sessionBox.getItems().add("Hiver");
        sessionBox.getItems().add("Ete");

        Button charger = new Button("charger");

        chargerPlane.getChildren().addAll(sessionBox, charger);

        leftPlane.getChildren().addAll(listeLabel, table, chargerPlane);
        charger.setOnAction( e -> {
            table.getItems().clear();
            Client client = new Client("127.0.0.1", 1337);
            client.run(0);
            client.requestCourses((String) sessionBox.getValue(), 0);
            try {
                client.disconnect();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            ArrayList<Course> requestedCourses = new ArrayList<>();
            requestedCourses = client.getCourses();
            int i = 0;
            while (requestedCourses.size() > i) {
                table.getItems().add(requestedCourses.get(i));
                i++;
            }
        });

        Label labelIns = new Label("Formulaire d'inscription");
        labelIns.setFont(new Font(25));
        labelIns.setPadding(new Insets(0, 0, padding, 0));

        GridPane formulaire = new GridPane();

        formulaire.add(new Label("Prénom"), 0, 0);
        TextField prenom = new TextField();
        formulaire.add(prenom, 1, 0);
        formulaire.add(new Label("Nom"), 0, 1);
        TextField nom = new TextField();
        formulaire.add(nom, 1, 1);
        formulaire.add(new Label("Email"), 0, 2);
        TextField email = new TextField();
        formulaire.add(email, 1, 2);
        formulaire.add(new Label("Matricule"), 0, 3);
        TextField matricule = new TextField();
        formulaire.add(matricule, 1, 3);
        Button sendForm = new Button("envoyer");
        sendForm.setAlignment(Pos.BOTTOM_RIGHT);

        formulaire.add(sendForm, 1, 4);
        formulaire.setHgap(width/30);
        formulaire.setVgap(height/35);
        Pane region = new Pane();
        region.setMinHeight(height/2);
        rightPlane.getChildren().addAll(labelIns, formulaire, region);

        sendForm.setOnAction(e -> {
            try {
                if (Integer.valueOf(matricule.getText()) > 99999999) {
                    throw new NumberFormatException();
                } else if (!verifyEmail(email.getText())) {
                    System.out.println("Email invalide: veuillez entrer un email @umontreal.ca");
                } else {
                    Client client = new Client("127.0.0.1", 1337);
                    client.run(0);
                    Course selectedCourse = table.getSelectionModel().getSelectedItem();
                    RegistrationForm newForm = new RegistrationForm(prenom.getText(), nom.getText(), email.getText(), matricule.getText(), selectedCourse);
                    try {
                        client.sendForm(newForm);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } catch (NumberFormatException ex) {
                System.out.println("Matricule invalide: not an int or greater than 8 characters");
            }

        });
        mainPlane.getChildren().addAll(leftPlane, rightPlane);
        stage.setScene(new Scene(mainPlane, width, height));
        stage.setResizable(false);
        stage.show();
    }

    private boolean verifyEmail(String email) {
        //marche po
        Pattern p = Pattern.compile("(.+?)"+"@umontreal.ca");
        Matcher mat = p.matcher(email);
        return mat.matches();
    }
}
