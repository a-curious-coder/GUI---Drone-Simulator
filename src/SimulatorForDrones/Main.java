package SimulatorForDrones;

// JavaFX Libraries
import apple.laf.JRSUIConstants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.application.Application;
import javafx.animation.AnimationTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private int sceneWidth = Settings.SCENE_WIDTH;
    private int sceneHeight = Settings.SCENE_HEIGHT;
    private Color sceneColour = Settings.BACKGROUND_COLOR;

    private FileChooser fc = new FileChooser();
    private Desktop desktop = Desktop.getDesktop();
    private Label droneInfo;
    private GridPane infoBar;
    BorderPane root = new BorderPane();                                         // Create container for entire GUI
    public Pane arena = new Pane();
    private static MenuBar menuBar = new MenuBar();
    private static GridPane controlGrid = new GridPane();

    /**
     *
     * @param primaryStage   - Applies these attribute settings to the stage it's given
     */
    @Override
    public void start(Stage primaryStage)  {
        AnimationTimer loop = startAnimation();

        //-------------------------------------------------
        //                  Menu
        //-------------------------------------------------
        Menu File = new Menu("File"),
                Edit = new Menu("Edit"),
                Other = new Menu("Other");

        MenuItem open = new MenuItem("Open"),
                 save = new MenuItem("Save"),
                 settings = new MenuItem("Settings"),
                 edit1 = new MenuItem("ExampleButton1"),
                 edit2 = new MenuItem("ExampleButton2"),
                 edit3 = new MenuItem("ExampleButton3"),
                 other1 = new MenuItem("Other option here");

        File.getItems().addAll(settings, open, save);
        Edit.getItems().addAll(edit1, edit2, edit3);
        Other.getItems().addAll(other1);

        menuBar.getMenus().addAll(File, Edit, Other);
        menuBar.setPrefHeight(20);
        settings.setOnAction(ActionEvent ->
        {
            TextInputDialog setting = new TextInputDialog("");
            setting.setTitle("Settings");
            setting.setHeaderText("Simulator Settings");
            setting.setContentText("Enter Drone Count: ");
            Optional<String> result = setting.showAndWait();
            result.ifPresent(droneNo -> System.out.println("Enter Drone Count" + droneNo));

        });

        open.setOnAction(ActionEvent ->
        {
            fc.setTitle("Open Drone Simulator File...");
            File file = fc.showOpenDialog(primaryStage);
            if(file != null)    {
                openFile(file);
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(new File("Drone.txt")))) {

                String line;
                while ((line = reader.readLine()) != null)
                    System.out.println(line);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        save.setOnAction(ActionEvent ->
        {

            fc.setTitle("Save Drone Positions");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", ".txt");
            fc.getExtensionFilters().add(extFilter);

            File file = fc.showSaveDialog(primaryStage);
            if (file != null)   {
                saveDronesToFile(droneInfo(), file);
            }
        });


        //--------------------------------
        //          GridPanes
        //--------------------------------

        GridPane sliderGrid = new GridPane();
        sliderGrid.setPrefSize(800, 100);
        GridPane buttonGrid = new GridPane();
        sliderGrid.setPrefSize(800, 50);

        //--------------------------------
        //          Button GridPane
        //--------------------------------
        Button start = new Button("Start"),
                stop = new Button("Stop"),
                reset = new Button("Reset Animation"),
                clear = new Button("Clear Drones"),
                init = new Button("Bring back Drones"),
                changeColour = new Button("Change Drone Colours"),
                addDrone = new Button("Add Drone"),
                addPredator = new Button("Add Predator"),
                addObstacle = new Button("Add Obstacle");
        // set button width
        start.setMinWidth(sceneWidth/3);
        stop.setMinWidth(sceneWidth/3);
        reset.setMinWidth(sceneWidth/3);
        clear.setMinWidth(sceneWidth/3);
        init.setMinWidth(sceneWidth/3);
        changeColour.setMinWidth(sceneWidth/3);
        addDrone.setMinWidth(sceneWidth/3);
        addPredator.setMinWidth(sceneWidth/3);
        addObstacle.setMinWidth(sceneWidth/3);

        buttonGrid.getColumnConstraints().add(new ColumnConstraints(sceneWidth/3));
        buttonGrid.getColumnConstraints().add(new ColumnConstraints(sceneWidth/3));
        buttonGrid.getColumnConstraints().add(new ColumnConstraints(sceneWidth/3));

        start.setOnAction(ActionEvent ->
        {
            loop.start();
        });

        stop.setOnAction(ActionEvent ->
        {
            loop.stop();
        });

        reset.setOnAction(ActionEvent ->
        {
            reinitDrones();
        });

        clear.setOnAction(ActionEvent -> { clearDrones(); });

        init.setOnAction(ActionEvent ->
        {

        });

        changeColour.setOnAction(ActionEvent ->
        {
            changeColour();
        });

        addDrone.setOnAction(ActionEvent ->
        {
            Drone drone = Drone.createDrone();
            arena.getChildren().add(drone);
        });

        addPredator.setOnAction(ActionEvent ->
        {
            Predator predator = new Predator();
            arena.getChildren().add(predator);
            getPredators().add(predator);
            getDrones().add(predator);

        });

        addObstacle.setOnAction(ActionEvent ->
        {
            Random rnd = new Random();
            Circle obstacle = new Circle(10, Color.GREEN);
            obstacle.setLayoutX(500*rnd.nextDouble());
            obstacle.setLayoutY(500 *rnd.nextDouble());
            arena.getChildren().add(obstacle);
            getObstacles().add(obstacle);
        });

        buttonGrid.add(start, 0,0);
        buttonGrid.add(stop, 0,1);
        buttonGrid.add(reset, 0,2);

        buttonGrid.add(clear, 1,0);
        buttonGrid.add(init, 1, 1);
        buttonGrid.add(changeColour, 1, 2);

        buttonGrid.add(addDrone, 2,0);
        buttonGrid.add(addPredator, 2,1);
        buttonGrid.add(addObstacle, 2,2);



        //--------------------------------
        //          Slider Grid
        //--------------------------------

        // Setting constraints
        // Cohesion Slider -----------
        Label cohesionLabel = new Label("Cohesion weight");
        cohesionLabel.setMinWidth(Settings.SCENE_WIDTH/3);
        cohesionLabel.setAlignment(Pos.CENTER);
        Slider cohesionSlider = new Slider(0, 10, Drone.COHESION_WEIGHT);
        cohesionSlider.setMaxWidth(Settings.SCENE_WIDTH/3 - 30);
        cohesionSlider.setBlockIncrement(0.1);
        cohesionSlider.setStyle("-fx-padding: 10px");
        final Label cohesionValue = new Label(Double.toString(cohesionSlider.getValue()));
        cohesionValue.setAlignment(Pos.CENTER);
        sliderGrid.add(cohesionLabel, 1, 0);
        sliderGrid.add(cohesionSlider, 1, 1);
        sliderGrid.add(cohesionValue, 1, 2);

        cohesionSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                Drone.COHESION_WEIGHT = newVal.doubleValue();
                cohesionValue.setText(String.format("%.1f", newVal));
            }
        });


        // Separation Slider -----------
        Label separationLabel = new Label("Separation weight");
        separationLabel.setMinWidth(Settings.SCENE_WIDTH/3);
        separationLabel.setAlignment(Pos.CENTER);
        Slider separationSlider = new Slider(0, 10, Drone.SEPARATION_WEIGHT);
        separationSlider.setMaxWidth(Settings.SCENE_WIDTH/3 - 30);
        separationSlider.setBlockIncrement(0.1);
        separationSlider.setStyle("-fx-padding: 10px");
        final Label separationValue = new Label(Double.toString(separationSlider.getValue()));
        separationValue.setAlignment(Pos.CENTER);
        sliderGrid.add(separationLabel, 3, 0);
        sliderGrid.add(separationSlider, 3, 1);
        sliderGrid.add(separationValue, 3, 2);

        separationSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                Drone.SEPARATION_WEIGHT = newVal.doubleValue();
                separationValue.setText(String.format("%.1f", newVal));
            }
        });


        // Alignment -----------
        Label alignmentLabel = new Label("Alignment weight");
        alignmentLabel.setMinWidth(Settings.SCENE_WIDTH/3 - 30);
        alignmentLabel.setAlignment(Pos.CENTER);
        Slider alignmentSlider = new Slider(0, 10, Drone.ALIGNMENT_WEIGHT);
        alignmentSlider.setMaxWidth(Settings.SCENE_WIDTH/3);
        alignmentSlider.setBlockIncrement(0.1);
        final Label alignmentValue = new Label(Double.toString(alignmentSlider.getValue()));
        alignmentSlider.setStyle("-fx-padding: 10px");
        alignmentValue.setAlignment(Pos.CENTER);
        sliderGrid.add(alignmentLabel, 5, 0);
        sliderGrid.add(alignmentSlider, 5, 1);
        sliderGrid.add(alignmentValue, 5, 2);

        alignmentSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                Drone.ALIGNMENT_WEIGHT = newVal.doubleValue();
                alignmentValue.setText(String.format("%.1f", newVal));
            }
        });

        //-------------------------------------------------
        //                  Drone Info Toolbar
        //-------------------------------------------------

        infoBar = new GridPane();
        Drone.initialiseDrones();                                                   // Creates drones, adds them to 'Drones' LinkedList
        arena.getChildren().addAll(getDrones());                                   // Adds drones to arena on GUI
        arena.setMaxSize(sceneWidth, sceneHeight);
        droneInfo = new Label(droneInfo());
        infoBar.add(droneInfo, 0, 0);
        infoBar.setPrefSize(210, 200);
        infoBar.setStyle("-fx-background-color:#eeeeee;-fx-opacity:1;");
        infoBar.setPadding(new Insets(10, 10, 10, 10));

        //-------------------------------------------------
        //                  GUI®
        //-------------------------------------------------

        root.setStyle("-fx-background-color: transparent;");                        // Sets border-pane background colour to transparent to allow scene colour to display
        arena.setPrefSize(sceneWidth, sceneHeight);                                 // set size for arena

        controlGrid.setStyle("-fx-background-color:#eeeeee;-fx-opacity:1;");
        controlGrid.add(sliderGrid, 0, 0);
        controlGrid.add(buttonGrid, 0, 1);

        root.setBottom(controlGrid);                                                 // Displays sliders controlling rule values, also buttons with start stop, etc./
        root.setRight(infoBar);                                                     // Displays drone information
        root.setTop(menuBar);                                                       // Displays menu bar at top
        root.setCenter(arena);                                                      // set layerPane to center of borderPane 'root'
        Scene scene = new Scene(root, sceneWidth, sceneHeight, sceneColour);        // defines settings for scene


        primaryStage.setTitle("Drone Simulator");                                   // set window title
        primaryStage.setScene(scene);                                               // Sets scene for stage
        primaryStage.show();                                                        // Shows stage.
        loop.start();
    }


    // ---------------------------------------
    //              Functions
    // ---------------------------------------

    private AnimationTimer startAnimation()   {

        AnimationTimer loop = new AnimationTimer() {

            @Override
            public void handle(long now) {

                Drone.moveDrones();
                getDrones().forEach(Drone::updateUI);
                updateInfo();


            }
        };

        return loop;
    }

    private void openFile(File file) {
        try{
            desktop.open(file);
        }   catch  (IOException ex) {
            Logger.getLogger(
                    Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void saveDronesToFile(String content, File file)   {
        try{
            PrintWriter writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex)    {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static List<Drone> getDrones() {
        return Drone.Drones;
    }

    public static List<Predator> getPredators() {
        return Drone.Predators;
    }

    public static List<Circle>  getObstacles() {
        return Drone.Obstacles;
    }

    private void reinitDrones() {

            clearDrones();
            Drone.initialiseDrones();
            arena.getChildren().addAll(getDrones());
    }

    private void changeColour() {
        for (Drone drone : getDrones())    {
            drone.setFill(Drone.randomColour());
        }
    }

    private void clearDrones()  {
        arena.getChildren().removeAll(getDrones());
        getDrones().clear();
    }

    private String droneInfo()   {

        StringBuilder output = new StringBuilder();
        int droneid = 1;

        for (Drone drone : getDrones()) {

            output.append(String.format("Drone : %d\tX: %.2f\tY: %.2f\n", droneid++, getLocation(drone).getX(), getLocation(drone).getY()));
        }
        return output.toString();
    }

    private static String ruleInfo()    {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < getDrones().size(); i++) {
            output.append("Cohesion: \t"     +      "value\n"   +
                          "Separation: \t"     +    "value\n"   +
                          "Alignment: \t"   +        "value\n"   );
        }

        return output.toString();
    }

    private void updateInfo()   {
            infoBar.getChildren().remove(droneInfo);
            droneInfo = new Label(droneInfo());
            infoBar.add(droneInfo, 0, 0);
    }

    private Point getLocation(Drone drone)    {
        return drone.location;
    }

    public static double getMenuBarHeight()  {
        return menuBar.getPrefHeight();
    }

    public static double getControlGridHeight()  {
        return controlGrid.getHeight();
    }
}



