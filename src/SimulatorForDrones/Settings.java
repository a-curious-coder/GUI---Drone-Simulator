package SimulatorForDrones;

import javafx.scene.paint.Color;

public class Settings {

    public static int SCENE_WIDTH = 600;
    public static int SCENE_HEIGHT = 500;

    public final static Color BACKGROUND_COLOR = Color.BLACK;

    public static int DRONE_COUNT = 10; // Number of drones
    public static double DRONE_MASS = 15; // Creates mass variable
    public static double DRONE_MAX_SPEED = 5;  // Velocity only increases, this restricts it from surpassing this value.
    public static double DRONE_MIN_DISTANCE = DRONE_MASS * 2d * 5; // 1 == arbitrary val
    public static double DRONE_INITIAL_VELOCITY = 1d;       // Gives each drone a base velocity as a minimum to start from.
    public static Color DRONE_COLOR = Color.WHITE;//Drone.randomColour();

    // ensure that attraction is applied with at least min and max
    // we don't want it to be too weak or too strong
    public static double MIN_ATTRACTION_DISTANCE = 5;
    public static double MAX_ATTRACTION_DISTANCE = 25.0;

    // Universal Gravitational Constant; irl:  6.67428E10-11;
    public static double GRAVITATIONAL_CONSTANT = 0.004;

}