package SimulatorForDrones;

// JavaFX Libraries
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {

    /**
     *
     * @param primaryStage   - Applies these attribute settings to the stage it's given
     */
    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();             // create containers

        Pane arena = new Pane();                        // arena for our sprites
        arena.setPrefSize(Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);

        Pane layerPane = new Pane();                    // Entire simulation represented by layers. These layers produce animation.
        layerPane.getChildren().addAll(arena);          // Gives the current layer whatever is contained within the arena pane
        root.setCenter(layerPane);                      // Sets layerpane to center of scene

        Scene scene = new Scene(root, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT); // Creates scene with desired size and settings
        primaryStage.setScene(scene);                                               // Sets scene for stage
        primaryStage.show();                                                        // Shows stage.

        Drone.initDrones(600, 600);
        //spawnDrones();                                // Add drones

        //startSimulator();                             // Run animation loop
    }
}
