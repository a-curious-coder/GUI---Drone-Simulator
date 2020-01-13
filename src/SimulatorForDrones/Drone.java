package SimulatorForDrones;

// JavaFX Libraries
import javafx.scene.paint.Color;  // Colour library
import javafx.scene.shape.Circle; // Imports Circle library and allows me to represent a drone with the circle.

// Java Libraries
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Drone extends Circle{ // Drone extends circle attributes - represented by circle.

    static List <Drone> Drones;   // ArrayList to hold all 'Drone' objects - Group of Drones
    static Random rnd = new Random();

    private static double sceneWidth = Settings.SCENE_WIDTH
                        , sceneHeight = Settings.SCENE_HEIGHT;
    private static int nDrones = 5;                        // Number of drones
    private static int radiusLimit = 10;                    // ----
    private static int velocityLimit = 15;
    private static int droneFlockRadius = 10;               // Flock Radius (size)
    private static int droneCounter = 0;

    private int droneID;
    private double v;
    
    private Vector location;                                // X, Y coords for drone
    private Vector velocity;                                // ... unsure

    public Color color = randomColor();

    private double droneRadius = 10; // Creates mass variable
    //private double maxSpeed = Settings.DRONE_MAX_SPEED; // Sets maxspeed from settings


    /**
     * 2 Drone constructors
     * Called dependant on what parameters are given upon request.
     */
    public Drone(int id, double x, double y, double v) {

        this.droneID = id;                          // Set drone ID
        this.v = v;                                 // Set velocity

        location = new Vector((int) x,(int) y);
        velocity = new Vector((int)v, (int)v);

        setRadius(droneRadius);
        setStroke(this.color);
        setFill(color.deriveColor(1, 1, 1, 0.2));

    }


    /**
    * returns a random colour
    */
    public static Color randomColor() {
        int range = 220;
        return Color.rgb((int) (rnd.nextDouble() * range), (int) (rnd.nextDouble() * range), (int) (rnd.nextDouble() * range));
    }

    public static void initialiseDrones()  {

    Drones = new ArrayList<>();

       for (int i = 0; i < nDrones; i++) {

            // random positions set.
            double x = rnd.nextDouble() * sceneWidth;
            double y = rnd.nextDouble() * sceneHeight;
             
            // Random velocity, based on speed
            double v = Math.random() * 4 + 1d; // 1d = initial velocity

            Drone drone = new Drone(i, x, y, v);

            Drones.add(drone);
            droneCounter = i+1;
            System.out.println("Drone: " + droneCounter + "X: " + x + " Y: " + y);
        }
    }

    /**
     * setRandomLocation function - sets the location of a drone to random value when constructor is called.
     * No two drones will have same position.
     *
     */
    private void setRandomLocation()    {

        Random rnd = new Random();
        location = new Vector();                                        // Instantiate location
        location.x = rnd.nextInt(Settings.SCENE_WIDTH);                           // location x element of vector set to random value * panel width
        location.y = rnd.nextInt(Settings.SCENE_HEIGHT);                          // "" y with height

        while(true) {

            for (Drone drone : Drones) {

                if (location.x == drone.location.x && location.y == drone.location.y) {     // if location.x and location.y are equal to any other drones x and y coords

                    location.x = rnd.nextInt(Settings.SCENE_WIDTH);               // Re-roll random x coordinate
                    location.y = rnd.nextInt(Settings.SCENE_HEIGHT);              // Re-roll random y coordinate
                } else {

                    return;                                             // Exit function as x and y coordinates are unique
                }
            }
        }

    }

    /**
     * MoveAllDrones function - Moves drones once per call
     *
     */
    public void MoveDrone()   {

        Vector rule1 = Cohesion(this);
        Vector rule2 = Separation(this);
        Vector rule3 = Alignment(this);

        // Adds values to velocity vector
        velocity.add(rule1);
        velocity.add(rule2);
        velocity.add(rule3);

        limitVelocity();

        // Adds velocity vector to the location vector
        location.add(velocity);

        constrainPosition();
    }


    private void constrainPosition()    {
        double xMin = droneRadius;
        double xMax = sceneWidth - droneRadius;
        double yMin = droneRadius;
        double yMax = sceneHeight - droneRadius;

        double x = location.x;
        double y = location.y;
        double vx = velocity.x;
        double vy = velocity.y;

        if( x < xMin) {                 // if x is less than xMin

            x = xMin;                   // x = xMin
            vx = v;                     // vx = v
        }
        else if( x > xMax) {            // if x is more than xMax

            x = xMax;                   // x == xMax
            vx = -v;                    // vx = -v
        }

        if( y < yMin) {                 // If y coordinate is less than the y Min (intersection another drones space I assume)

            y = yMin;                   // y becomes yMin
            vy = v;                     // vy becomes v
        }
        else if( y > yMax) {            // If y coordinate is more than the y Max then

            y = yMax;                   // y coordinate reset to yMax
            vy = -v;                    // velocity y = -v
        }
    }

    public void updateUI() {
        setCenterX(location.x);
        setCenterY(location.y);
    }

    /**
     * Cohesion function
     * Searches for a perceived center in a 'flock' of drones.
     * @return return newly calculated perceived center
     */
    private Vector Cohesion(Drone drone)  {

        Vector pc = new Vector(0, 0); // perceived center, instantiated.

        for (Drone neighbour : Drones)    {                 // For each drone in Drones ArrayList

            if (drone == neighbour)                   // if Drone is NOT equal to any other drone
                continue;

                pc.add(neighbour.location);

        }

        if(Drones.size() > 1)   {
            double div = 1d / (Drones.size() - 1);
            pc.multiply(div);
        }

        pc.subtract(drone.location);
        pc.multiply(0.01);

        return pc;  // new perceived center
    }

    /**
     * Separation function
     * Keeps a minimum distance between drones when they're moving around. Ensures they don't collide.
     *
     * @return return perceived center
     */
    private Vector Separation(Drone drone) {

        Vector pc = new Vector(0, 0);

        for (Drone neighbour : Drones)  {

            if (drone.equals(neighbour)) {                  // if Drone is NOT equal to any other drone

                if(isClose(drone.location)) {

                    //Vector sub = new Vector(drone.location);
                    neighbour.location.subtract(drone.location);
                    pc.subtract(neighbour.location);
                }
            }
        }

        return pc;
    }

    /**
     * Alignment function
     * Ensures velocity amongst drones is close to or matches velocity or nearby drones
     *
     * @return perceived velocity value
     */
    private Vector Alignment(Drone drone) {

        Vector pv = new Vector(0, 0); // Perceived Velocity

        for(Drone neighbour : Drones)   {

            if(drone.equals(neighbour)) {

                pv.add(neighbour.velocity);
            }
        }

        if(Drones.size() > 1)   {
            double div = 1d / (Drones.size() - 1);
            pv.multiply(div);
        }

        pv.subtract(drone.velocity);
        pv.multiply(0.125);

        return pv;
    }

    private void limitVelocity() {
        if (velocity.length() > velocityLimit) {

            velocity.divide(velocity.length());
            velocity.multiply(velocityLimit);
        }
    }


    private boolean isInDroneFlock(final Vector loc) {

        Vector range = new Vector(location);
        range.subtract(loc);

        return range.length() < droneFlockRadius;
    }


    private boolean isClose(final Vector loc) {
        Vector range = new Vector(location);
        range.subtract(loc);                                             // Subtracts location vector passed from range
        return range.length() < radiusLimit;
    }
}