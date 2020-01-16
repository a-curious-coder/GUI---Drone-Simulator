package SimulatorForDrones;

// JavaFX Libraries
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.application.Application;
import javafx.animation.AnimationTimer;
// JavaFX Menu Libraries
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {

    private int sceneWidth = Settings.SCENE_WIDTH;
    private int sceneHeight = Settings.SCENE_HEIGHT;
    private Color sceneColour = Settings.BACKGROUND_COLOR;

    /**
     *
     * @param primaryStage   - Applies these attribute settings to the stage it's given
     */
    @Override
    public void start(Stage primaryStage) throws InterruptedException {

        MenuBar menuBar = new MenuBar();
        VBox vbox = new VBox(menuBar);

        final ContextMenu contextMenu = new ContextMenu();

        BorderPane root = new BorderPane();                                         // Create container
        root.setStyle("-fx-background-color: transparent;");                        // Sets border-pane background colour to transparent to allow scene colour to display

        Pane arena = new Pane();                                                    // arena for our drones
        arena.setPrefSize(sceneWidth, sceneHeight);                                 // set size for arena

        root.setCenter(arena);                                                      // set layerPane to center of borderPane 'root'

        Scene scene = new Scene(root, sceneWidth, sceneHeight, sceneColour);        // defines settings for scene

        scene.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.SECONDARY) {
                Drone.Drones.clear();
                Drone.initialiseDrones();
            }
            if(e.getButton() == MouseButton.PRIMARY) {
                arena.getChildren().removeAll(Drone.Drones);
                Drone.Drones.clear();

                Drone.initialiseDrones();
                arena.getChildren().addAll(Drone.Drones);
            }
        });

        primaryStage.setTitle("Drone Simulator");                                   // set window title
        primaryStage.setScene(scene);                                               // Sets scene for stage
        primaryStage.show();                                                        // Shows stage.

        // Creates drones, adds them to 'Drones' LinkedList
        Drone.initialiseDrones();

        // Adds drones to arena on GUI
        arena.getChildren().addAll(Drone.Drones);


        AnimationTimer loop = new AnimationTimer() {

            @Override
            public void handle(long now) {

                Drone.Drones.forEach(Drone::MoveDrone);
                Drone.Drones.forEach(Drone::updateUI);

            }
        };

        loop.start();
    }
}
