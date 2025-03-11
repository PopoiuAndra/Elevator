package org.example.clientel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ClientApplication extends Application {

    private HttpClient httpClient = HttpClient.newHttpClient();
    private Integer currentFloor = 0;
    private int idElevator = 2;

    @Override
    public void start(Stage primaryStage) {
        Label responseLabel = new Label("Response will be displayed here");
        Button goInTheBuildingButton = new Button("Mergi la lift!");

        VBox root = new VBox(10, goInTheBuildingButton, responseLabel);
        root.alignmentProperty().setValue(javafx.geometry.Pos.TOP_CENTER);

        Image image = new Image("file:src/main/resources/cladire.jpg");
        BackgroundSize backgroundSize = new BackgroundSize(500, 500, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        root.setBackground(new Background(backgroundImage));


        // ASTA TREBUIE TRANSFORMATA IN FUNCTIE PENTRU A PUTEA FI REFOLOSITA
        goInTheBuildingButton.setOnAction(event -> {
            goInBuildingAction(root);
        });


        Scene scene = new Scene(root, 500, 500);
        primaryStage.setTitle("Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void goInBuildingAction(VBox root)
    {

        Button upDirectionButton = new Button("UP");
        Button downDirectionButton = new Button("DOWN");

        upDirectionButton.setOnAction(event -> {
            fetchDataFromServer(root);
        });

        Image newImage = new Image("file:src/main/resources/hallway.jpg");
        BackgroundSize backgroundSize = new BackgroundSize(500, 500, true, true, true, false);
        BackgroundImage newBackgroundImage = new BackgroundImage(newImage, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        root.setBackground(new Background(newBackgroundImage));

        // Remove the old buttons
        root.getChildren().clear();
        root.setAlignment(Pos.BOTTOM_RIGHT);

        Label responseLabel = new Label("Floor");
        root.getChildren().addAll(responseLabel);

        // Add the new buttons to the VBox
        root.getChildren().addAll(upDirectionButton);
        root.getChildren().addAll(downDirectionButton);
    }

    private void fetchDataFromServer(VBox root) {
        Label responseLabel = new Label("Response will be displayed here");
        root.getChildren().add(responseLabel);

        System.out.println("Current floor: " + currentFloor);
        // aici fol un api pentru a afla cel mai apropiat lift
        String uri = "http://localhost:1520/elevator/";
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(uri + "direction/" + currentFloor + "/UP"))
                .GET()
                .build();

        // calculez pozitia la care va trebui afisata valoarea??????????? - tine de afisare

        CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request2, HttpResponse.BodyHandlers.ofString());
        response.thenAccept(httpResponse -> {
            System.out.println("Am intrat aqui");
            final String[] responseBody = {httpResponse.body()};
            idElevator = Integer.parseInt(responseBody[0]);

            System.out.println( " Response body : " + responseBody[0]);
            System.out.println("Id Lift: " + idElevator);;

            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create(uri  + "update/" +  idElevator + "/" + currentFloor))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            CompletableFuture<HttpResponse<Void>> response2 = httpClient.sendAsync(request3, HttpResponse.BodyHandlers.discarding());
            response2.thenAccept(httpResponse2 -> {
                System.out.println("Request sent successfully, response code: " + httpResponse2.statusCode());

            });

            // PLACE HOLDER - aici textul poate fi inlocuit intr un while cu etajul la care se afla liftul
            // Update the label on the JavaFX Application Thread
            while(!responseBody[0].equals(currentFloor.toString()))
            {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(uri + "floor/" + idElevator))
                        .GET()
                        .build();
                CompletableFuture<HttpResponse<String>> response3 = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                    response3.thenAccept(httpResponse3 -> {
                    responseBody[0] = httpResponse3.body();
                    });
                javafx.application.Platform.runLater(() -> responseLabel.setText(responseBody[0]));
            }

            inElevator(root);
        });
    }


    // FUNCTIE PENTRU CAND AJUNGE IN LIFT + BUTOANELE DIN LIFT
    public void inElevator(VBox root){
            Platform.runLater(() -> {
            root.getChildren().clear();

            Button button0 = new Button("P");
            Button button1 = new Button("1");
            Button button2 = new Button("2");
            Button button3 = new Button("3");
            Button button4 = new Button("4");
            Button button5 = new Button("5");

            button0.setOnAction(event -> {
                elevatorButtonHandler(0, root);
            });
            button1.setOnAction(event -> {
                elevatorButtonHandler(1, root);
            });
            button2.setOnAction(event -> {
                elevatorButtonHandler(2, root);
            });
            button3.setOnAction(event -> {
                elevatorButtonHandler(3, root);
            });
            button4.setOnAction(event -> {
                elevatorButtonHandler(4, root);
            });
            button5.setOnAction(event -> {
                elevatorButtonHandler(5, root);
            });


            // SCHIMBA BACKGORUND
            BackgroundSize backgroundSize = new BackgroundSize(500, 500, true, true, true, false);
            Image newImage = new Image("file:src/main/resources/inElevator.jpg");
            BackgroundImage newBackgroundImage = new BackgroundImage(newImage, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
            root.setBackground(new Background(newBackgroundImage));

            // APAR BUTOANELE PENTRU A ALEGE ETAJUL
            root.getChildren().addAll(button0, button1, button2, button3, button4, button5);

        });
    }


    // FUNCTIE PENTRU CAND AJUNGE IN LIFT + BUTOANELE DIN LIFT
    private void elevatorButtonHandler(int floorWanted, VBox root)
    {
        // ADAUGA STOPUL LA ETAJUL VRUT
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:1520/elevator/update/"  + idElevator + "/" + floorWanted))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        CompletableFuture<HttpResponse<Void>> response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding());

        Platform.runLater(() -> {
            root.getChildren().clear();

            // SCHIMBA BACKGORUND
            BackgroundSize backgroundSize = new BackgroundSize(500, 500, true, true, true, false);
            Image newImage = new Image("file:src/main/resources/inElevator.jpg");
            BackgroundImage newBackgroundImage = new BackgroundImage(newImage, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
            root.setBackground(new Background(newBackgroundImage));

            // ADAUGA LABELUL CARE AFISEAZA LA CE ETAJ E LIFTUL
            Label responseLabel = new Label("Liftul se afla la etajul " + currentFloor);

            root.getChildren().addAll(responseLabel);


            String uri = "http://localhost:1520/elevator/";
            Integer gottenFloor;
            /*
            while(currentFloor != floorWanted){

                // VA APELA UN API CARE VA VERIFICA DACA A AJUNS LA ETAJUL DORIT
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri + "floor/" + idElevator))
                    .GET()
                    .build();
                CompletableFuture<HttpResponse<String>> response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                response.thenAccept(httpResponse -> {
                    int responseBody = Integer.parseInt(httpResponse.body());
                    gottenFloor = responseBody;
                });
                // SCHIMBA currentfloor. DACA A AJUNS LA ETAJUL DORIT ATUNCI IESA DIN WHILE
                currentFloor = gottenFloor;
                // DACA NU FACE UN WAIT 1 sec DUPA CARE SCHIMBA LABELUL SI REINCEPE WHILEUL
                waitFor(1000);
            }

            // SE VA REAPELA FUNCTIA APELATA (HANDELUL BBUTONULUI) IN MOMENTUL IN CARE ESTE APASAT BUTONUL DE LA INCEPUTUL PROGRAMULUI
            goInBuildingAction(root);
            */
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}