package SimulatorForDrones;

// JavaFX Libraries
import javafx.scene.paint.Color;  // Colour library
import javafx.scene.shape.Circle; // Imports Circle library and allows me to represent a drone with the circle.

// Java Libraries
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javafx.geometry.Point2D;

public class Drone extends Circle{ // Drone extends circle attributes - represented by circle.

    static List <Drone> Drones;   // ArrayList to hold all 'Drone' objects - Group of Drones
    static Random rnd = new Random();

    private static double sceneWidth = Settings.SCENE_WIDTH
                        , sceneHeight = Settings.SCENE_HEIGHT;
    private static int nDrones = Settings.DRONE_COUNT;                        // Number of drones
    private static int radiusLimit = 10;                    // ----
    private static int velocityLimit = 15;
    private static int droneFlockRadius = 10;               // Flock Radius (size)
    private static int droneCounter = 0;

    private int droneID;
    private double v;
    
    private Point2D location;                                // getX(), getY() coords for drone
    private Point2D velocity;                                // ... unsure

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

        location = new Point2D(x, y);
        velocity = new Point2D((int)v, (int)v);

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
            System.out.println("Drone: " + droneCounter + "getX(): " + x + " getY(): " + y);
        }
    }

    /**
     * setRandomLocation function - sets the location of a drone to random value when constructor is called.
     * No two drones will have same position.
     *
     */
    private void setRandomLocation()    {

        Random rnd = new Random();
        location = new Point2D(rnd.nextDouble() * sceneWidth, rnd.nextInt() * sceneHeight);   // Instantiate location// location getX() element of vector set to random value * panel width         // "" getY() with height

        while(true) {

            for (Drone drone : Drones) {

                if (location.getX() == drone.location.getX() && location.getY() == drone.location.getY()) {     // if location.getX() and location.getY() are equal to any other drones getX() and getY() coords

                    double x = rnd.nextDouble() * sceneWidth;
                    double y = rnd.nextDouble() * sceneHeight;

                    location = new Point2D(x ,y);
                                // Re-roll random getY() coordinate
                } else {

                    return;                                             // Exit function as getX() and getY() coordinates are unique
                }
            }
        }

    }

    /**
     * MoveAllDrones function - Moves drones once per call
     *
     */
    public void MoveDrone()   {

        for(int i = 0; i < nDrones; i++)   {

            Drone drone = Drones.get(i);

            Point2D rule1 = Cohesion(drone);
            Point2D rule2 = Separation(drone);
            Point2D rule3 = Alignment(drone);

            System.out.println("Rule 1 = " + rule1.getX() + " " + rule1.getY() + "\t" +
                               "Rule 2 = " + rule2.getX() + " " + rule2.getY() + "\t" +
                               "Rule 3 = " + rule3.getX() + " " + rule3.getY());
            // Adds values to velocity vector
            drone.velocity = drone.velocity.add(rule1)
                                           .add(rule2)
                                           .add(rule3);
            System.out.println("drone.velocity + rule1 = " + drone.velocity.getX() + " " + drone.velocity.getY());
            System.out.println("drone.velocity + rule2 = " + drone.velocity.getX() + " " + drone.velocity.getY());
            System.out.println("drone.velocity + rule3 = " + drone.velocity.getX() + " " + drone.velocity.getY());
            //limitVelocity();

            // Adds velocity vector to the location vector
            drone.location = drone.location.add(velocity);
            System.out.println("drone.location + velocity = " + drone.location.getX() + " " + drone.location.getY());
            //constrainPosition();
        }

    }


    private void constrainPosition(Drone drone)    {
        double xMin = droneRadius;
        double xMax = sceneWidth - droneRadius;
        double yMin = droneRadius;
        double yMax = sceneHeight - droneRadius;

        double x = drone.location.getX();
        double y = drone.location.getY();
        double vx = drone.velocity.getX();
        double vy = drone.velocity.getY();

        if( x < xMin) {                 // if getX() is less than xMin

            x = xMin;                   // getX() = xMin
            vx = v;                     // vx = v
        }
        else if( x > xMax) {            // if getX() is more than xMax

            x = xMax;                   // getX() == xMax
            vx = -v;                    // vx = -v
        }

        if( y < yMin) {                 // If getY() coordinate is less than the getY() Min (intersection another drones space I assume)

            y = yMin;                   // getY() becomes yMin
            vy = v;                     // vy becomes v
        }
        else if( y > yMax) {            // If getY() coordinate is more than the getY() Max then

            y = yMax;                   // getY() coordinate reset to yMax
            vy = -v;                    // velocity getY() = -v
        }
    }

    /*
    Point2D v = new Point2D();

        if (drone.location.getX() < xMin)    {
            v.getX() = 10;
        }
        else if (drone.location.getX() > xMax)   {
            v.getX() = -10;
        }

        if (drone.location.getY() < yMin) {
            v.getY() = 10;
        }
        else if (drone.location.getY() > yMax)   {
            v.getY() = -10;
        }


        return v;
     */


    public void updateUI() {
        setCenterX(location.getX());
        setCenterY(location.getY());
    }

    /**
     * Cohesion function
     * Searches for a perceived center in a 'flock' of drones.
     * @return return newly calculated perceived center
     */
    private Point2D Cohesion(Drone drone)  {

        Point2D pc = new Point2D(0, 0); // perceived center, instantiated.
        for (Drone neighbour : Drones)    {             // For each drone in Drones ArrayList

            if (neighbour != drone)                     // if Drone is NOT equal to any other drone
                continue;
               pc = pc.add(neighbour.location);             // Add all drone location vectors
        }

        if(Drones.size() > 1)   {
            double div = 1d/(Drones.size() - 1);// Divide by number of drones - 1 (averages the overall perceived center value and finds the general center value)
            pc = pc.multiply(div);                           //
        }
        pc = pc.subtract(drone.location);
        pc = pc.multiply(0.01);

        System.out.println("Cohesion:\t" + pc);
        return pc;  // new perceived center
    }

    /**
     * Separation function
     * Keeps a minimum distance between drones when they're moving around. Ensures they don't collide.
     *
     * @return return perceived center
     */
    private Point2D Separation(Drone drone) {

        Point2D pc = new Point2D(0, 0);

        for (Drone neighbour : Drones)  {

            if (neighbour != drone) {                  // if Drone is NOT equal to any other drone

                if(isClose(drone.location)) {

                    //Point2D sub = new Point2D(drone.location);
                    neighbour.location = neighbour.location.subtract(drone.location);
                    pc = pc.subtract(neighbour.location);
                }
            }
        }
        System.out.println("Separation:\t" + pc);
        return pc;
    }

    /**
     * Alignment function
     * Ensures velocity amongst drones is close to or matches velocity or nearby drones
     *
     * @return perceived velocity value
     */
    private Point2D Alignment(Drone drone) {

        Point2D pv = new Point2D(0, 0); // Perceived Velocity

        for(Drone neighbour : Drones)   {

            if(drone.equals(neighbour)) {

                pv = pv.add(neighbour.velocity);
            }
        }

        if(Drones.size() > 1)   {
            double div = 1d / (Drones.size() - 1);
            pv = pv.multiply(div);
        }

        pv = pv.subtract(drone.velocity);
        pv = pv.multiply(0.125);

        System.out.println("Alignment:\t" + pv);
        return pv;
    }

    /*private void limitVelocity() {

        double v = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getY() * velocity.getY());
        if (v > velocityLimit) {

            velocity.divide(v);
            velocity.multiply(velocityLimit);
        }
    }*/

    /*
    private boolean isInDroneFlock(final Point2D loc) {

        Point2D range = new Point2D(location);
        range.subtract(loc);

        return range.length() < droneFlockRadius;
    }*/


    private boolean isClose(final Point2D loc) {
        Point2D range = new Point2D(location.getX(), location.getY());
        range = range.subtract(loc);                                             // Subtracts location vector passed from range
        double v = Math.sqrt(range.getX() * range.getX() + range.getY() * range.getY());
        return v < radiusLimit;
    }
}