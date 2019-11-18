package application;

import javafx.animation.TranslateTransition;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class controller {

    public void welcomeToProgram() {
        Alert a1 = new Alert(Alert.AlertType.INFORMATION);
                a1.setTitle("About");
                //a1.setHeaderText("About the program");
                a1.setContentText("27007990 - Drone Simulator program.\nOwned by Callum McLennan");
                a1.showAndWait();
    }

    public void MoveCircle()    {
        Circle cir = new Circle();
        cir.setFill(Color.BLACK);
        cir.setRadius(30);
        cir.setLayoutX(60);
        cir.setLayoutY(80);

        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(3));
        transition.setToX(500);
        transition.setToY(500);
        transition.setNode(cir); // ??
        transition.play();
    }



    public void fpsCounter()    {

    }
}
