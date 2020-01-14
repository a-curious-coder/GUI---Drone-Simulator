package SimulatorForDrones;

// JavaFX Libraries
import javafx.scene.paint.Color;  // Colour library
import javafx.scene.shape.Circle; // Imports Circle library and allows me to represent a drone with the circle.
import javafx.geometry.Point2D;

// Java Libraries
import java.awt.*;
import java.util.*;
import java.util.List;

public class Drone extends Circle   { // Drone extends circle attributes - represented by circle.

    static List <Drone> Drones;   // ArrayList to hold all 'Drone' objects - Group of Drones
    static Random rnd = new Random();

    private static double initialVelocity = Settings.DRONE_INITIAL_VELOCITY;
    private static double sceneHeight = Settings.SCENE_HEIGHT;
    private static double sceneWidth = Settings.SCENE_WIDTH;
    private static int nDrones = Settings.DRONE_COUNT;                        // Number of drones
    private double droneRadius = Settings.DRONE_MASS;                         // Creates mass variable
    private static Color color = randomColour();                              // public = different colours   -   private static = same colour, random everytime
    private static int droneFlockRadius = 10;                                 // Flock Radius (size)
    private static int velocityLimit = 1000;                                  // ----
    private static int radiusLimit = 100;                                     // ----
    private int droneID;                                                      // Holds the ID for drone - for printing information
    private double v;                                                         // v holds the individual value for velocity.

    private Point2D location;                                                 // Stores X and Y coordinates for drone
    private Point2D velocity;                                                 // Determines speed of drone - helps with next location


    /**
     *
     * @param id    Drone ID
     * @param x     X coordinate for drone spawn
     * @param y     Y coordinate for drone spawn
     * @param v     V is the velocity of the drone
     *
     */
    public Drone(int id, double x, double y, double v) {

        this.droneID = id;                          // Set drone ID
        this.v = v;                                 // Set velocity

        location = new Point2D(x, y);               // Set location
        velocity = new Point2D(v, v);               // Set velocity

        setRadius(droneRadius);                     // Sets drone radius (mass/size)
        setStroke(this.color);                      // Sets drone edge colour
        setFill(color.deriveColor(1, 1, 1, 0.2));   // Sets drone internal colour.

    }


    /**
    * Returns a random colour
    */
    public static Color randomColour() {

        int range = 220;
        return Color.rgb((int) (rnd.nextDouble() * range), (int) (rnd.nextDouble() * range), (int) (rnd.nextDouble() * range));
    }

    /**
     * Generates number of drones and stores them to Drones ArrayList
     * Generates random variables as suitable parameters for the Drone constructor
     * x and y values are generated to remain within the parameters of the scene in the GUI
     *
     */
    public static void initialiseDrones()  {

    Drones = new ArrayList<>();

       for (int i = 0; i < nDrones; i++) {

            // Setting Random Positions
            double x = rnd.nextDouble() * sceneWidth;
            double y = rnd.nextDouble() * sceneHeight;
            //double x = setRandomLocation().getX();                     - Something I'm working on
            //double y = setRandomLocation().getY();

            // Random velocity, based on initial velocity
            double v = Math.random() * 4 + initialVelocity;              // 1d = initial velocity

            Drone drone = new Drone(i, x, y, v);

            Drones.add(drone);

        }
    }

    /**
     * setRandomLocation function - sets the location of a drone to random value when constructor is called.
     * No two drones will have same position.
     *
     */
    public static Point2D setRandomLocation()    {

        Random rnd = new Random();
        Point2D loc = new Point2D(rnd.nextDouble() * sceneWidth, rnd.nextInt() * sceneHeight);   // Instantiate location// location getX() element of vector set to random value * panel width         // "" getY() with height

        while(true) {

            for (Drone neighbour : Drones) {

                if (loc.getX() == neighbour.location.getX() && loc.getY() == neighbour.location.getY()) {     // if location.getX() and location.getY() are equal to any other drones getX() and getY() coords

                    double x = rnd.nextDouble() * sceneWidth;
                    double y = rnd.nextDouble() * sceneHeight;

                    loc = new Point2D(x ,y);
                                // Re-roll random getY() coordinate
                } else {

                    break;                                             // Exit function as getX() and getY() coordinates are unique
                }
            }
            return loc;
        }

    }

    /**
     * MoveAllDrones function - Moves drones once per call
     *
     */
    public void MoveDrone()   {

        Point2D rule1 = Cohesion(this);
        Point2D rule2 = Separation(this);
        Point2D rule3 = Alignment(this);


            // Adds values to velocity vector
        velocity = velocity
                .add(rule1)
                .add(rule2)
                .add(rule3)
                ;

        limitVelocity();

        // Adds velocity vector to the location vector
        location = location.add(velocity);
           
        constrainPosition(this);    // Sets constraints to how far out the drones can move - cannot move outside scene
    }

    /**
     *
     * @param drone     Requires drone object for it's x and y coordinates
     *                  This is to calculate whether x and y coordinates are within
     *                  the scene contained in the GUI Pane
     *
     */
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

        // TODO: Find less power consuming solution: find out how to modify the vector directly or create own Point2D class
        location = new Point2D( x, y);
        velocity = new Point2D( vx, vy);

    }

    /**
     * updates the user interface - location x and y coordinates update for the drones - allows movement.
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

        Point2D pc = new Point2D(0, 0);          // perceived center, instantiated.

        for (Drone neighbour : Drones)    {             // For each drone in Drones ArrayList

            if (drone == neighbour)                     // if Drone is equal to any other drone
                continue;

               pc = pc.add(neighbour.location);         // Add all drone location vectors
        }

        if(Drones.size() > 1)   {
            double div = 1d/(Drones.size() - 1);        // Divide by number of drones - 1 (averages the overall perceived center value and finds the general center value)
            pc = pc.multiply(div);                           //
        }
        pc = pc.subtract(drone.location).multiply(0.01);

        //System.out.println("Cohesion:\t" + pc);
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

        for (Drone neighbour : Drones) {

            if (drone == neighbour)                   // if Drone is NOT equal to any other drone
                continue;

            if ((neighbour.location.getX() - drone.location.getX()) < 100 && (neighbour.location.getY() - drone.location.getY()) < 100) {
                pc = pc.subtract(neighbour.location.subtract(drone.location));
            }

            //System.out.println("Separation:\t" + pc);

        }
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

            if(drone == neighbour) {

                pv = pv.add(neighbour.velocity);
            }
        }

        if(Drones.size() > 1)   {
            double div = 1d / (Drones.size() - 1);
            pv = pv.multiply(div);
        }

        pv = (pv.subtract(drone.velocity)).multiply(0.125);

        //System.out.println("Alignment:\t" + pv);
        return pv;
    }
/*
    private Point2D align(ArrayList<Drone> Drones)  {
        Point2D sum = new Point2D(0,0);

        for(Drone neighbour : Drones)   {
            sum.add(neighbour.velocity);
        }
        sum = new Point2D(sum.getX() / Drones.size(), sum.getY() / Drones.size());

        sum.magnitude();

        Point2D steer = new Point2D(0, 0);
        steer.subtract(sum).subtract(velocity);

        return steer;
    }

    PVector align (ArrayList<Boid> boids) {
        Add up all the velocities and divide by the total to calculate the average velocity.
                PVector sum = new PVector(0,0);
        for (Boid other : boids) {
            sum.add(other.velocity);
        }
        sum.div(boids.size());

        We desire to go in that direction at maximum speed.
        sum.setMag(maxspeed);

        Reynolds’s steering force formula
        PVector steer = PVector.sub(sum,velocity);
        steer.limit(maxforce);
        return steer;
    }*/

    /**
     * limits the velocity for each drone - ensures they don't gain infinite speed.
     */
    private void limitVelocity() {
        if (velocity.magnitude() > velocityLimit) {
            velocity =  velocity.multiply(1d/(velocity.magnitude())).multiply(velocityLimit);
        }
    }


    private boolean isClose(Point2D location) {
        Point2D range = new Point2D(location.getX(), location.getY());
        range = range.subtract(location);                                             // Subtracts location vector passed from range
        return range.magnitude() < radiusLimit;
    }
}