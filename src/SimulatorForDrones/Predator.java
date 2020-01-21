package SimulatorForDrones;

import javafx.scene.paint.Color;

public class Predator extends Drone {
    private double maxSpeed = Settings.PREDATOR_MAX_SPEED;

    // Constructor of predator
    public Predator() {
        super(300,300);
        setFill(Settings.PREDATOR_COLOUR);
        setRadius(Settings.PREDATOR_RADIUS);
        setMaxSpeed(Settings.PREDATOR_MAX_SPEED);
    }
}

