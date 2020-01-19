package SimulatorForDrones;

// JavaFX Libraries
import apple.laf.JRSUIConstants;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.application.Application;
import javafx.animation.AnimationTimer;

import java.text.DecimalFormat;

public class Main extends Application {

    private int sceneWidth = Settings.SCENE_WIDTH;
    private int sceneHeight = Settings.SCENE_HEIGHT;
    private Color sceneColour = Settings.BACKGROUND_COLOR;

    private Label droneInfo, ruleInfo;
    public static Slider cohesionSlider, separationSlider, alignmentSlider;
    private ToolBar infoBar;
    private Pane arena = new Pane();

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

        cohesionSlider = new Slider(0, 1, 0.5);
        cohesionSlider.setPrefWidth(200d);
        cohesionSlider.setOrientation(Orientation.HORIZONTAL);
        cohesionSlider.setLayoutX(500);
        cohesionSlider.setLayoutY(500);
        separationSlider = new Slider(0, 1, 0.5);
        alignmentSlider = new Slider(0, 1, 0.5);

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
        //                  Drone Info Toolbar
        //-------------------------------------------------Slider

        infoBar = new ToolBar();
        Drone.initialiseDrones();                                                   // Creates drones, adds them to 'Drones' LinkedList
        arena.getChildren().addAll(Drone.Drones);                                   // Adds drones to arena on GUI

        droneInfo = new Label(droneInfo());
        ruleInfo = new Label(ruleInfo());
        infoBar.getItems().addAll(droneInfo, cohesionSlider, separationSlider, alignmentSlider);
        infoBar.setOrientation(Orientation.VERTICAL);
        //-------------------------------------------------
        //                  GUI
        //-------------------------------------------------
        BorderPane root = new BorderPane();                                         // Create container
        root.setStyle("-fx-background-color: transparent;");                        // Sets border-pane background colour to transparent to allow scene colour to display
                                                                                    // arena for our drones
        arena.setPrefSize(sceneWidth, sceneHeight);                                 // set size for arena

        root.setBottom(toolBar);                                                    // Displays buttons with start stop, etc.
        root.setRight(infoBar);                                                     // Displays drone information
        root.setTop(menuBar);                                                       // Displays menu bar at top
        root.setCenter(arena);                                                      // set layerPane to center of borderPane 'root'
        root.getChildren().add(cohesionSlider);
        Scene scene = new Scene(root, sceneWidth, sceneHeight, sceneColour);        // defines settings for scene

        primaryStage.setTitle("Drone Simulator");                                   // set window title
        primaryStage.setScene(scene);                                               // Sets scene for stage
        primaryStage.show();                                                        // Shows stage.

        startAnimation();


    }


    private void startAnimation()   {
        AnimationTimer loop = new AnimationTimer() {

            @Override
            public void handle(long now) {

                Drone.Drones.forEach(Drone::MoveDrone);
                Drone.Drones.forEach(Drone::updateUI);
                updateInfo();

            }
        };

        loop.start();
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

    private String droneInfo()   {

        StringBuilder output = new StringBuilder();
        int droneid = 1;

        for (Drone drone : Drone.Drones) {

            output.append(String.format("Drone : %s\tX: %.2f\tY: %.2f\n", droneid++, getLocation(drone).getX(), getLocation(drone).getY()));
            //output.append(String.format("Drone " + new DecimalFormat("00").format(droneid++) + "\tx: "
                                                 //+ new DecimalFormat("0.00").format(getLocation(drone).getX()) + "\ty: "
                                                 //+ new DecimalFormat("0.00").format(getLocation(drone).getY()) + "\n"));
        }
        return output.toString();
    }

    private static String ruleInfo()    {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < Drone.Drones.size(); i++) {
            int droneCounter = i + 1;
            output.append("Cohesion: \t"     +      "value\n"   +
                          "Separation: \t"     +    "value\n"   +
                          "Alignment: \t"   +        "value\n"   );
        }

        return output.toString();
    }

    private void updateInfo()   {
        infoBar.getItems().clear();
        droneInfo = new Label(droneInfo());
        //ruleInfo = new Label(ruleInfo());
        infoBar.getItems().addAll(droneInfo);
    }

    private Point2D getLocation(Drone drone)    {
        return drone.location;
    }
}



