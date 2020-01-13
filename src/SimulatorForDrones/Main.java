package SimulatorForDrones;

// JavaFX Libraries
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {

    private static List<Drone> Drones = Drone.Drones;
    int sceneWidth = Settings.SCENE_WIDTH;
    int sceneHeight = Settings.SCENE_HEIGHT;
    Color sceneColour = Settings.BACKGROUND_COLOR;

    Pane arena;
    /**
     *
     * @param primaryStage   - Applies these attribute settings to the stage it's given
     */
    @Override
    public void start(Stage primaryStage) throws InterruptedException {

        BorderPane root = new BorderPane();                                         // Create container
        Pane layerPane = new Pane();                                                // Entire simulation represented by layers. These layers produce animation.

        arena = new Pane();                                                         // arena for our drones
        arena.setPrefSize(sceneWidth, sceneHeight);                                 // set size for arena

        root.setCenter(layerPane);                                                  // set layerPane to center of borderPane 'root'

        Scene scene = new Scene(root, sceneWidth, sceneHeight, sceneColour);        // defines settings for scene


        primaryStage.setTitle("Drone Simulator");                                   // set window title
        primaryStage.setScene(scene);                                               // Sets scene for stage
        primaryStage.show();                                                        // Shows stage.

        // Creates drones, adds them to 'Drones' ArrayList
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
