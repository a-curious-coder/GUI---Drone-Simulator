package SimulatorForDrones;

// JavaFX Libraries
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    private static List Drones = Drone.Drones;
    /**
     *
     * @param primaryStage   - Applies these attribute settings to the stage it's given
     */
    @Override
    public void start(Stage primaryStage) throws InterruptedException {

        BorderPane root = new BorderPane();             // create containers
        Pane layerPane = new Pane();                    // Entire simulation represented by layers. These layers produce animation.
        Pane arena = new Pane();                        // arena for our sprites
        Scene scene = new Scene(root, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT); // Creates scene with desired size and settings

        Circle circle = new Circle(4, Color.RED);
        circle.relocate(Settings.SCENE_WIDTH/2, Settings.SCENE_HEIGHT/2);

        arena.setPrefSize(Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);
        Drone.initialiseDrones(Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);

        for(int i = 0; i < Drones.size(); i++)   {
            System.out.println("Drone: " + Drone.Drones.get(i));
            //layerPane.getChildren().add();
        }

        layerPane.getChildren().addAll(arena, circle);          // Gives the current layer whatever is contained within the arena pane
        root.setCenter(layerPane);                      // Sets layerpane to center of scene

        primaryStage.setTitle("Drone Simulator");
        primaryStage.setScene(scene);                                               // Sets scene for stage
        primaryStage.show();                                                        // Shows stage.


        //spawnDrones();                                // Add drones

        //startSimulator();                             // Run animation loop
    }
}
