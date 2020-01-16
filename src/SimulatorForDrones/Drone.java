package SimulatorForDrones;

// JavaFX Libraries
import javafx.scene.paint.Color;  // Colour library
import javafx.scene.shape.Circle; // Imports Circle library and allows me to represent a drone with the circle.
import javafx.geometry.Point2D;

// Java Libraries
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class Drone extends Circle   { // Drone extends circle attributes - represented by circle.

    static List <Drone> Drones = new LinkedList<Drone>();   // LinkedList to hold all 'Drone' objects - Group of Drones
    static Random rnd = new Random();

    private static double initialVelocity = Settings.DRONE_INITIAL_VELOCITY;
    private static double sceneHeight = Settings.SCENE_HEIGHT;
    private static double sceneWidth = Settings.SCENE_WIDTH;
    private static int nDrones = Settings.DRONE_COUNT;
    private double droneRadius = Settings.DRONE_MASS;
    private static Color color = randomColour();                              // public = different colours   -   private static = same colour, random everytime
    private static int droneFlockRadius = 10;                                 // Flock Radius (size)
    private static double velocityLimit = Settings.DRONE_MAX_SPEED;
    private static int radiusLimit = 1;                                       // ----
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
     * Generates number of drones and stores them to Drones LinkedList
     * Generates random variables as suitable parameters for the Drone constructor
     * x and y values are generated to remain within the parameters of the scene in the GUI
     *
     */
    public static void initialiseDrones()  {

    Drones = new LinkedList<Drone>();

       for (int i = 0; i < nDrones; i++) {

            // Setting Random Positions
            Point2D loc = setRandomLocation();
            double x = loc.getX();
            double y = loc.getY();
            int droneCounter = i + 1;
            System.out.println("Drone " + droneCounter + "\tX: " + new DecimalFormat("0.00") .format(x) + "     Y: " + new DecimalFormat("0.00") .format(y));
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
        Point2D loc = new Point2D(rnd.nextInt((int)sceneWidth), rnd.nextInt((int)sceneHeight));   // Instantiate location


            for (Drone neighbour : Drones) {

                if (loc.getX() == neighbour.location.getX() && loc.getY() == neighbour.location.getY()) {     // if location.getX() and location.getY() are equal to any other drones getX() and getY() coords

                    double x = rnd.nextInt((int)sceneWidth);
                    double y = rnd.nextInt((int)sceneHeight);

                    loc = new Point2D(x, y);
                    // Re-roll random x and y coordinate
                }
            }

            return loc;
    }

    /**
     * MoveAllDrones function - Moves drones once per call
     *
     */
    public void MoveDrone()   {

        Point2D rule1 = Cohesion(this);
        Point2D rule2 = Separation(this);
        Point2D rule3 = Alignment(this);
        Point2D rule4 = bound_position(this);

            // Adds values to velocity vector
        velocity = velocity
                .add(rule1)
                .add(rule2)
                .add(rule3)
                .add(rule4)
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
        double xMax = sceneWidth;
        double yMin = droneRadius;
        double yMax = sceneHeight;

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
     * Due to limitations of Point2D library, you cannot individually set X and Y values, only both.
     * The issue here is that if y is holding a value and you want to only set X, you need to reset the Y value.
     * This function stores the Y value and sets x whilst passing the stored y value - maintaining all values necessary
     *
     * @param v     The vector being passed in
     * @param n     The number that's going to be used in setting the x value
     * @return      Vector containing values
     */
    private Point2D setX(Point2D v, double n)  {
       double y  = v.getY();    // Stores the Y value of the Point2D vector
       v = new Point2D(n, y);
       return v;
    }

    /**
     * ""
     * @param v     ""
     * @param n     ""
     * @return      Vector containing values
     */
    private Point2D setY(Point2D v, double n)  {
        double x  = v.getX();    // Stores the Y value of the Point2D vector
        v = new Point2D(x, n);
        return v;
    }



    private Point2D bound_position(Drone drone)    {
        double xMin = droneRadius, xMax = sceneWidth, yMin = droneRadius, yMax = sceneHeight;
        int pX = 10, nX = -10, pY = 10, nY = -10;

        Point2D v = new Point2D(0, 0);

        if(drone.location.getX() < xMin)    {
            setX(v, pX);
        } else if (drone.location.getX() > xMax)    {
            setX(v, nX);
        }

        if(drone.location.getY() < yMin)    {
            setY(v, pY);
        } else if (drone.location.getY() > yMax)    {
            setY(v, nY);
        }

        return v;
    }

    /**
     * updates the user interface - location x and y coordinates update for the drones - allows movement.
     */
    void updateUI() {
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

        for (Drone neighbour : Drones)    {             // For each drone in Drones LinkedList

            if (drone == neighbour)                     // if Drone is equal to any other drone
                continue;

               pc = pc.add(neighbour.location);         // Add all drone location vectors
        }

        if(Drones.size() > 1)   {
            //double div = 1d/(Drones.size() - 1);        // Divide by number of drones - 1 (averages the overall perceived center value and finds the general center value)
            //pc = pc.multiply(div);                           //
            Point2D_Div(pc, Drones.size()-1);
        }
        pc = pc.subtract(drone.location).multiply(0.01);

        System.out.println("Cohesion:\t" + "\tX: " + new DecimalFormat("0.00") .format(pc.getX()) + "    Y:" + new DecimalFormat("0.00") .format(pc.getY()));
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

            if (neighbour.location != drone.location) {

                double x = (neighbour.location).getX() - drone.location.getX();
                double y = (neighbour.location).getY() - drone.location.getY();
                double v = x + y;
                System.out.println("x" + " + "+ "y" + " = " + v);

                Point2D n = new Point2D(x, y);
                System.out.println("x" + " , "+ "y" + " = " + n);

                if (v > 0 && v < 100) {

                    System.out.println("if (v > 0 && v < 100)");
                    System.out.println("pc  " + pc + " -  n = " + new DecimalFormat("0.00").format(n.getX()) + " + " + new DecimalFormat("0.00").format(n.getY()));

                    pc = pc.subtract(n);
                }
            }
        }
        System.out.println("pc:\t" + pc);
        System.out.println("\nSeparation:\t" + "\tX: " + new DecimalFormat("0.00").format(pc.getX()) + "    Y:" + new DecimalFormat("0.00").format(pc.getY()));
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

        System.out.println("Alignment:\t" + "\tX: " + new DecimalFormat("0.00") .format(pv.getX()) + "    Y:" + new DecimalFormat("0.00") .format(pv.getY()));
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

    /**
     *  flock is a group of drones that move according to 3 rules
     *  can be multiple flocks
     * @return  drones within the specific flock
     */
    public List<Drone> flock()  {
        List<Drone> flock = new LinkedList<Drone>();
            for(Drone drone : Drones)   {
                if(isInFlock(drone.location))   {
                    flock.add(drone);
                }
            }
            return flock;
    }

    /**
     * Checks whether drone is within flock area
     * @param loc   location of drone
     * @return  ??
     */
    private boolean isInFlock(Point2D loc)  {
        Point2D range = new Point2D(location.getX(), location.getY());
        range.subtract(loc);
        return range.magnitude() < droneFlockRadius;
    }

    private boolean isClose(Point2D location) {
        Point2D range = new Point2D(location.getX(), location.getY());
        range = range.subtract(location);                                             // Subtracts location vector passed from range
        //System.out.println("range.magnitude() < radiusLimit\t" + range.magnitude() + " < " + radiusLimit);
        if(range.magnitude() < radiusLimit) {
            //System.out.println("true");
            return true;
        }
        return range.magnitude() < radiusLimit;
    }

    private Point2D Point2D_Div(Point2D v1, int A)  {
        Point2D v = new Point2D(0,0);
        double vX = v.getX();
        double vY = v.getY();
        setX(v, v1.getX() / A);
        setY(v, v1.getY() / A);

        return v;
    }
}