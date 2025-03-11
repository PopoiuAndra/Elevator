package org.example.clientel;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientApplication extends Application {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private Integer currentFloor = 0;
    private int idElevator=1;
    private boolean isInElevator = false;
    private final Stage stage=new Stage();
    double windowWidth ;
    double windowHeight;

    private int clientId;
    private boolean isElevatorOccupied = false;



    @Override
    public void start(Stage primaryStage) {
        registerClient();
        Label responseLabel = new Label("Response will be displayed here");
        Button goInTheBuildingButton = new Button("Go to elevator!");

        VBox root = new VBox(10, goInTheBuildingButton, responseLabel);
        root.setAlignment(Pos.TOP_CENTER);

        setBackground(root, "file:src/main/resources/building.jpg");

        goInTheBuildingButton.setOnAction(event -> goInBuildingAction(root));
        stage.setTitle("Client");

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        windowWidth = primaryScreenBounds.getWidth() * 0.7;
        windowHeight = primaryScreenBounds.getHeight();
        stage.setWidth(windowWidth);
        stage.setHeight(windowHeight);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    private void setBackground(VBox root, String imagePath) {
        Image image = new Image(imagePath);
        BackgroundSize backgroundSize = new BackgroundSize(windowWidth, windowHeight, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        root.setBackground(new Background(backgroundImage));
    }

    private void goInBuildingAction(VBox root) {
        Button upDirectionButton = new Button("UP");
        Button downDirectionButton = new Button("DOWN");

        upDirectionButton.setOnAction(event -> fetchDataFromServer(root, "UP"));
        downDirectionButton.setOnAction(event -> fetchDataFromServer(root, "DOWN"));

        setBackground(root, "file:src/main/resources/hallway.jpg");
        idElevator=1;

        root.getChildren().clear();
        root.setAlignment(Pos.BOTTOM_RIGHT);

        Label responseLabel = new Label("Floor");
        root.getChildren().addAll(responseLabel, upDirectionButton, downDirectionButton);
    }

    private void fetchDataFromServer(VBox root, String direction) {
        Label responseLabel = new Label("Requesting elevator...");
        root.getChildren().add(responseLabel);

        if (isElevatorOccupied) {
            Platform.runLater(() -> {
                Label responseLabel1 = new Label("The elevator is currently occupied. Please wait.");
                root.getChildren().add(responseLabel1);
            });
            return;
        }

        String uri = "http://localhost:1520/elevator/";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri + "direction/" + currentFloor + "/" + direction))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(httpResponse -> {
            String responseBody = httpResponse.body();
            System.out.println("Received response body: " + responseBody);
            idElevator = Integer.parseInt(responseBody.trim());

            HttpRequest updateRequest = HttpRequest.newBuilder()
                    .uri(URI.create(uri + "update/" + idElevator + "/" + currentFloor))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            httpClient.sendAsync(updateRequest, HttpResponse.BodyHandlers.discarding()).thenAccept(updateResponse -> {
                Platform.runLater(() -> {
                    isInElevator = true;
                    isElevatorOccupied=true;
                    root.getChildren().clear();
                    inElevator(root);
                });
            });
        });
    }

    private void inElevator(VBox root) {
        isInElevator = true;
        Platform.runLater(() -> {
            root.getChildren().clear();

            BorderPane borderPane = new BorderPane();

            // Adăugăm textul într-un chenar frumos în partea de sus
            Label titleLabel = new Label("Choose the floor you want to go to:");
            titleLabel.setStyle("-fx-background-color: #f4f4f4; " +
                    "-fx-border-color: #ccc; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 10px; " +
                    "-fx-padding: 10px;");
            borderPane.setTop(titleLabel);

            GridPane buttonGrid = new GridPane(); // Creăm un GridPane pentru a aranja butoanele într-o matrice

            Button[] buttons = new Button[6];
            for (int i = 0; i < 6; i++) {
                int floor = i;
                buttons[i] = new Button(floor == 0 ? "P" : String.valueOf(i));
                buttons[i].setOnAction(event -> elevatorButtonHandler(floor, root));
                buttons[i].setStyle("-fx-background-color: #686D76; " +
                        "-fx-border-color: #373A40; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 30px; " +
                        "-fx-background-radius: 30px; " +
                        "-fx-font-weight: bold;");
                buttons[i].setMinWidth(50); // Setăm o lățime minimă pentru butoane
                buttons[i].setMinHeight(50); // Setăm o înălțime minimă pentru butoane

                // Calculăm coordonatele butoanelor în matrice
                int row = i / 3;
                int col = i % 3;

                // Adăugăm butonul la GridPane, la poziția specificată
                buttonGrid.add(buttons[i], col, row);
            }

            // Setăm spațiul orizontal și vertical între celulele din GridPane
            buttonGrid.setHgap(20);
            buttonGrid.setVgap(10);

            // Aliniem GridPane-ul orizontal și vertical în centru
            buttonGrid.setAlignment(Pos.TOP_CENTER);

            // Setăm o înălțime minimă pentru fiecare rând de butoane, astfel încât să se alinieze în mijloc vertical
            RowConstraints rowConstraints = new RowConstraints();
            RowConstraints rowConstraints1 = new RowConstraints();

            rowConstraints.setMinHeight(300);
            rowConstraints1.setMinHeight(25);

            buttonGrid.getRowConstraints().addAll(rowConstraints1, rowConstraints1, rowConstraints);

            borderPane.setCenter(buttonGrid);

            setBackground(root, "file:src/main/resources/inElevator.jpg");

            root.getChildren().add(borderPane);

            root.getChildren().add(buttonGrid); // Adăugăm GridPane-ul în VBox-ul root
        });
    }


    private void elevatorButtonHandler(int floorWanted, VBox root) {
        isInElevator = true;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:1520/elevator/update/" + idElevator + "/" + floorWanted))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding()).thenRun(() -> {
            Platform.runLater(() -> {
                root.getChildren().clear();
                setBackground(root, "file:src/main/resources/inElevator.jpg");

                Label responseLabel = new Label("The elevator is now at floor " + currentFloor);
                root.getChildren().add(responseLabel);

                String uri = "http://localhost:1520/elevator/";
                updateCurrentFloor(root, floorWanted, responseLabel, uri);
            });
        });
    }

    private void updateCurrentFloor(VBox root, int floorWanted, Label responseLabel, String uri) {
        CompletableFuture.runAsync(() -> {
            AtomicBoolean arrived = new AtomicBoolean(false);
            while (!arrived.get()) {
                HttpRequest floorRequest = HttpRequest.newBuilder()
                        .uri(URI.create(uri + "floor/" + idElevator))
                        .GET()
                        .build();
                httpClient.sendAsync(floorRequest, HttpResponse.BodyHandlers.ofString()).thenAccept(floorResponse -> {
                    int newFloor = Integer.parseInt(floorResponse.body().trim());
                    if (newFloor != currentFloor) {
                        currentFloor = newFloor;
                        System.out.println("The elevator is now at floor " + currentFloor);
                        Platform.runLater(() -> responseLabel.setText("The elevator is now at floor " + currentFloor));
                    }
                    if (newFloor == floorWanted) {
                        arrived.set(true);
                    }
                }).join();
                try {
                    Thread.sleep(1000); // Wait for 1 second before the next check
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(() -> {
                isInElevator = false;
                setFloorBackground(root, floorWanted);
                showHallway(root);
            });
        });
    }

    private void showHallway(VBox root) {
        Platform.runLater(() -> {
            root.getChildren().clear();
            setFloorBackground(root, currentFloor);

            String floorMessage;
            switch (currentFloor) {
                case 0:
                    floorMessage = "You are at the ground floor hallway.";
                    break;
                case 1:
                    floorMessage = "You are at the 1st floor hallway.";
                    break;
                case 2:
                    floorMessage = "You are at the 2nd floor hallway.";
                    break;
                case 3:
                    floorMessage = "You are at the 3rd floor hallway.";
                    break;
                case 4:
                    floorMessage = "You are at the 4th floor hallway.";
                    break;
                case 5:
                    floorMessage = "You are at the 5th floor hallway.";
                    break;
                default:
                    floorMessage = "You are at an unknown floor.";
                    break;
            }
            isElevatorOccupied = false;
            Label messageLabel = new Label(floorMessage);
            root.getChildren().add(messageLabel);
            Button upDirectionButton = new Button("UP");
            Button downDirectionButton = new Button("DOWN");
            upDirectionButton.setOnAction(event -> fetchDataFromServer(root, "UP"));
            downDirectionButton.setOnAction(event -> fetchDataFromServer(root, "DOWN"));
            Button goToYourRoomButton = new Button("Go to your room");
            goToYourRoomButton.setOnAction(event->leaveElevaotr(root));
            root.getChildren().addAll(upDirectionButton, downDirectionButton, goToYourRoomButton);
        });
    }

    private void leaveElevaotr(VBox root) {
        String uri = "http://localhost:1520/elevator/client/" + clientId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .DELETE()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenRun(() -> {
            Platform.runLater(() -> {
                System.out.println("Client removed: " + clientId);
                isElevatorOccupied = false;
                stage.close();
            });
        });
    }
    private void setFloorBackground(VBox root, int floor) {
        switch (floor) {
            case 0:
                setBackground(root, "file:src/main/resources/hallway.jpg");
                break;
            case 1:
                setBackground(root, "file:src/main/resources/etaj1.jpg");
                break;
            case 2:
                setBackground(root, "file:src/main/resources/etaj2.jpg");
                break;
            case 3:
                setBackground(root, "file:src/main/resources/etaj3.jpg");
                break;
            case 4:
                setBackground(root, "file:src/main/resources/etaj4.jpg");
                break;
            case 5:
                setBackground(root, "file:src/main/resources/etaj5.jpg");
                break;
        }
    }

    public void registerClient() {
        String uri = "http://localhost:1520/elevator/client";
        String requestBody = "{\n" +
                "\"name\": \"Client\",\n" +
                "\"currentFloor\": " + currentFloor + ",\n" +
                "\"idElevator\": " + idElevator + "\n" +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(httpResponse -> {
            String responseBody = httpResponse.body();
            System.out.println("Received response body: " + responseBody);
            // Parse the response to extract the client ID
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                clientId = jsonNode.get("id").asInt();
                System.out.println("Assigned client ID: " + clientId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        }



    public static void main(String[] args) {
        launch(args);
    }
}
