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

import javax.swing.*;
import java.util.ArrayList;

public class client_fx extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        Client client = new Client("127.0.0.1", 1337);
        client.run(0);
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

        TableView table = new TableView();
        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Course, String> coursCol = new TableColumn("Cours");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.getColumns().addAll(codeCol, coursCol);
        // Faire fit les columns sur la longueur du tab
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        HBox chargerPlane = new HBox();
        chargerPlane.setAlignment(Pos.CENTER);
        ChoiceBox sessionBox = new ChoiceBox();
        sessionBox.getItems().add("Automne");
        sessionBox.getItems().add("Hiver");
        sessionBox.getItems().add("Été");

        Button charger = new Button("charger");
        charger.setOnAction( e -> {
            client.requestCourses((String) sessionBox.getValue(), 0);
            ArrayList<Course> requestedCourses = new ArrayList<>();
            requestedCourses = client.getCourses();
            int i = 0;
            while (requestedCourses.size() > i) {
                    table.getItems().add(requestedCourses.get(i));
                i++;
            }
        });

        chargerPlane.getChildren().addAll(sessionBox, charger);

        leftPlane.getChildren().addAll(listeLabel, table, chargerPlane);

        Label labelIns = new Label("Formulaire d'inscription");
        labelIns.setFont(new Font(25));

        GridPane formulaire = new GridPane();

        formulaire.add(new Label("Prénom"), 0, 0);
        formulaire.add(new TextField(), 1, 0);
        formulaire.add(new Label("Nom"), 0, 1);
        formulaire.add(new TextField(), 1, 1);
        formulaire.add(new Label("Email"), 0, 2);
        formulaire.add(new TextField(), 1, 2);
        formulaire.add(new Label("Matricule"), 0, 3);
        formulaire.add(new TextField(), 1, 3);
        Button sendForm = new Button("envoyer");
        formulaire.add(sendForm, 1, 4);

        sendForm.setAlignment(Pos.CENTER);
        rightPlane.getChildren().addAll(labelIns, formulaire);

        mainPlane.getChildren().addAll(leftPlane, rightPlane);
        stage.setScene(new Scene(mainPlane, width, height));
        stage.setResizable(false);
        stage.show();
    }
}
