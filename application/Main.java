package application;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.shape.*;
import javafx.util.Duration;

/**
 * Main function extends Application from Application Library from JavaFX
 */
public class Main extends Application {
    public static Stage stage;
    int time = 900;
    private ArrayList<Circle> circles;
    @Override
    public void start(Stage primaryStage) {

        stage = primaryStage;
        try {

            // Load the FXML document (we created with SceneBuilder)
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation( Main.class.getResource("Scene.fxml") );

            // Load the layout from the FXML and add it to the scene
            BorderPane layout = (BorderPane) loader.load();



            Circle cir = new Circle();
            cir.setFill(Color.BLACK);
            cir.setRadius(10);
            cir.setLayoutX(60);
            cir.setLayoutY(80);

            // Functions
            translate(cir); // Moves shape positions
            scale(cir); // Changes size of shape over
            colour(cir); // Changes colour of shape over time


            layout.getChildren().add(cir);
            Scene scene = new Scene(layout, 600, 600);

            // Set the scene to stage and show the stage to the user
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }


    }
    public void translate(Circle cir) {
        TranslateTransition transition = new TranslateTransition(); // Type of action that can move shape.
        transition.setDuration(Duration.millis(time));
        transition.setToX(0);
        transition.setToY(500);
        //transition.setAutoReverse(true); // Allows for reversal of animation
        //transition.setCycleCount(3); // Iterations of animation - makes use of reversal
        transition.setNode(cir); // ??
        transition.play(); // Plays animation

        transition.setDuration(Duration.millis(time));
        transition.setToX(500);
        transition.setToY(0);
        transition.setNode(cir);
        transition.play();

        transition.setDuration(Duration.millis(time));
        transition.setToX(0);
        transition.setToY(-500);
        transition.setNode(cir);
        transition.play();
    }
    public void scale(Circle cir)   {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(time), cir);
        scaleTransition.setCycleCount(Animation.INDEFINITE);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setToX(3);
        scaleTransition.setToY(3);
        scaleTransition.play();
    }
    public void colour(Circle cir)    {
        FillTransition fillTransition = new FillTransition(Duration.millis(time), cir, Color.YELLOW, Color.GREEN);
        fillTransition.setCycleCount(Animation.INDEFINITE);
        fillTransition.setAutoReverse(true);
        fillTransition.play();
    }
    public static void main(String[] args) {
        launch(args);
    }
}


