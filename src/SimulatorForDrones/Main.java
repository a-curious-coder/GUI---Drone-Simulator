package SimulatorForDrones;

// JavaFX Libraries
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.application.Application;
import javafx.animation.AnimationTimer;

public class Main extends Application {

    private int sceneWidth = Settings.SCENE_WIDTH;
    private int sceneHeight = Settings.SCENE_HEIGHT;
    private Color sceneColour = Settings.BACKGROUND_COLOR;
    private boolean startstop = true;

    Pane arena = new Pane();

    /**
     *
     * @param primaryStage   - Applies these attribute settings to the stage it's given
     */
    @Override
    public void start(Stage primaryStage) throws InterruptedException {

        //-------------------------------------------------
        //                  Menu
        //-------------------------------------------------
        Menu File = new Menu("File"),
                Edit = new Menu("Edit"),
                Other = new Menu("Other");

        MenuItem file1 = new MenuItem("Settings"),
                 file2 = new MenuItem("About"),
                 file3 = new MenuItem("Help"),
                 edit1 = new MenuItem("Cohesion"),
                 edit2 = new MenuItem("Separation"),
                 edit3 = new MenuItem("Alignment"),
                 other1 = new MenuItem("Other option here");

        File.getItems().addAll(file1, file2, file3);
        Edit.getItems().addAll(edit1, edit2, edit3);
        Other.getItems().addAll(other1);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(File, Edit, Other);
        //-------------------------------------------------
        //                  Toolbar
        //-------------------------------------------------
        ToolBar toolBar = new ToolBar();
        Button start = new Button("Start"),
                stop = new Button("Stop"),
                reset = new Button("Reset Animation"),
                init = new Button("Initialise Drones");


        start.setOnAction(ActionEvent ->
        {
            System.out.println("Start");
        });
        stop.setOnAction(ActionEvent ->
        {
            System.out.println("Stop");
        });
        reset.setOnAction(ActionEvent ->
        {
            reinitDrones(false);
        });

        toolBar.getItems().addAll(start, stop, reset);

        //-------------------------------------------------
        //                  GUI
        //-------------------------------------------------
        BorderPane root = new BorderPane();                                         // Create container
        root.setStyle("-fx-background-color: transparent;");                        // Sets border-pane background colour to transparent to allow scene colour to display
                                                          // arena for our drones
        arena.setPrefSize(sceneWidth, sceneHeight);       // set size for arena

        root.setBottom(toolBar);
        root.setTop(menuBar);
        root.setCenter(arena);                                                      // set layerPane to center of borderPane 'root'

        Scene scene = new Scene(root, sceneWidth, sceneHeight, sceneColour);        // defines settings for scene

        primaryStage.setTitle("Drone Simulator");                                   // set window title
        primaryStage.setScene(scene);                                               // Sets scene for stage
        primaryStage.show();                                                        // Shows stage.

        Drone.initialiseDrones();                                                   // Creates drones, adds them to 'Drones' LinkedList
        arena.getChildren().addAll(Drone.Drones);                                   // Adds drones to arena on GUI

        startAnimation(startstop);


    }


    private void startAnimation(boolean startstop)   {
        AnimationTimer loop = new AnimationTimer() {

            @Override
            public void handle(long now) {

                Drone.Drones.forEach(Drone::MoveDrone);
                Drone.Drones.forEach(Drone::updateUI);

            }
        };

        if(startstop)   {
            loop.start();
        } else if (startstop == false)  {
            loop.stop();
        }

    }

    private void reinitDrones(boolean changeColour) {
        if(changeColour)    {

            arena.getChildren().removeAll(Drone.Drones);
            Drone.Drones.clear();
            Drone.initialiseDrones();

            for(int i = 0; i < Drone.Drones.size(); i++) {

                Drone.Drones.get(i).setFill(Drone.randomColour());
            }

            arena.getChildren().addAll(Drone.Drones);

        }  else {

            arena.getChildren().removeAll(Drone.Drones);
            Drone.Drones.clear();
            Drone.initialiseDrones();
            arena.getChildren().addAll(Drone.Drones);

        }
    }
}



