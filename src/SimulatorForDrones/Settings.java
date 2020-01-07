package SimulatorForDrones;

import java.awt.*;

public class Settings {

    public static int SCENE_WIDTH = 500;
    public static int SCENE_HEIGHT = 500;
    public final static Color BACKGROUND_COLOR = Color.WHITE;

    public static int DRONE_COUNT = 40;

    public static double DRONE_MAX_SPEED = 20;
    public static double DRONE_MASS = 10;

    // ensure that attraction is applied with at least min and max
    // we don't want it to be too weak or too strong
    public static double MIN_ATTRACTION_DISTANCE = 5;
    public static double MAX_ATTRACTION_DISTANCE = 25.0;

    // Universal Gravitational Constant; irl:  6.67428E10-11;
    public static double GRAVITATIONAL_CONSTANT = 0.004;

}