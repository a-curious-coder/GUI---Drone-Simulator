package SimulatorForDrones;

// JavaFX Libraries
import javafx.scene.paint.Color;  // Colour library
import javafx.scene.shape.Circle; // Imports Circle library and allows me to represent a drone with the circle.
import javafx.geometry.Point2D;

// Java Libraries
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class Drone extends Circle   { // Drone extends circle attributes - represented by circle.

    public static List <Drone> Drones = new LinkedList<Drone>();   // LinkedList to hold all 'Drone' objects - Group of Drones
    private static Random rnd = new Random();

    private static double initialVelocity = Settings.DRONE_INITIAL_VELOCITY;
    private static double sceneHeight = Settings.SCENE_HEIGHT;
    private static double sceneWidth = Settings.SCENE_WIDTH;
    private static int numberOfDrones = Settings.DRONE_COUNT;
    private static double droneRadius = Settings.DRONE_MASS;
    private static Color color = Settings.DRONE_COLOR;                        // public = different colours   -   private static = same colour, random everytime
    private static int droneFlockRadius = 10000;                                 // Flock Radius (size)
    private static double velocityLimit = Settings.DRONE_MAX_SPEED;
    private static int radiusLimit = 1;                                       // ----
    private double v;                                                         // v holds the individual value for velocity.
    private double maxForce = 5;

    private Point2D location;                                                 // Stores X and Y coordinates for drone
    private Point2D velocity;                                                 // Determines speed of drone - helps with next location
    private Point2D acceleration;

    /**
     *
     * @param x     X coordinate for drone spawn
     * @param y     Y coordinate for drone spawn
     * @param v     V is the velocity of the drone
     *
     */
    public Drone(int id, double x, double y, double v) {

        id = Drones.size() + 1;                // Set drone ID
        this.v = v;                                 // Set velocity


        //location = new Point2D(x, y);
        location = new Point2D(sceneWidth/2, sceneHeight/2);
        velocity = new Point2D(v, v);               // Set velocity
        acceleration = new Point2D(0,0);


        setRadius(droneRadius);                     // Sets drone radius (mass/size)
        setStroke(this.color);                      // Sets drone edge colour
        setFill(color.deriveColor(1, 1, 1, 0.2));   // Sets drone internal colour.

    }


    public static void createDrone()   {
        int id = Drones.size() + 1;
        Point2D loc = setRandomLocation();
        double x = loc.getX();
        double y = loc.getY();
        double v = Math.random() * 4 + initialVelocity;

        Drone drone = new Drone(id, x, y, v);
        Drones.add(drone);
        System.out.println("Drone: " + id + "\t\tX: " + loc.getX() + "\tY: " + loc.getY());
    }

    /**
     * Generates number of drones and stores them to Drones LinkedList
     * Generates random variables as suitable parameters for the Drone constructor
     * x and y values are generated to remain within the parameters of the scene in the GUI
     *
     */
    public static void initialiseDrones()  {

       for (int i = 0; i < numberOfDrones; i++) {
            createDrone();
        }
    }

    /**
     * MoveAllDrones function - Moves drones once per call
     *
     */
    public void MoveDrone()   {

            //Point2D rule1 = Cohesion(Drones);
            //Point2D rule2 = Separation(Drones);
            //Point2D rule3 = Alignment(Drones);
            //Point2D rule4 = bound_position(this);

            // Adds values to velocity vector
            velocity = velocity
                    //.add(rule1)       // Cohesion
                    //.add(rule2)         // Separation
                    //.add(rule3)       // Alignment
                    //.add(rule4)       // Boundposition
            ;

            limitVelocity();

            location = edges(location);
            // Adds velocity vector to the location vector
            location = location.add(velocity);
            velocity = velocity.add(acceleration);
            //constrainPosition(this);    // Sets constraints to how far out the drones can move - cannot move outside scene
    }

    /**
     * Returns a random colour
     */
    public static Color randomColour() {

        int range = 220;
        return Color.rgb((int) (rnd.nextDouble() * range), (int) (rnd.nextDouble() * range), (int) (rnd.nextDouble() * range));
    }

    /**
     * setRandomLocation function - sets the location of a drone to random value when constructor is called.
     * No two drones will have same position.
     *
     */
    public static Point2D setRandomLocation()    {

        Random rnd = new Random();
        Point2D loc = new Point2D(rnd.nextInt((int)sceneWidth) + droneRadius, rnd.nextInt((int)sceneHeight) + droneRadius);   // Instantiate location


            for (Drone neighbour : Drones) {

                if (loc.getX() == neighbour.location.getX() + droneRadius && loc.getY() == neighbour.location.getY() + droneRadius) {     // if location.getX() and location.getY() are equal to any other drones getX() and getY() coords

                    double x = rnd.nextInt((int)sceneWidth) + droneRadius;
                    double y = rnd.nextInt((int)sceneHeight) + droneRadius;

                    loc = new Point2D(x, y);
                    // Re-roll random x and y coordinate
                }
            }

            return loc;
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
       double x  = v.getX();    // Stores the Y value of the Point2D vector
       v = new Point2D(n, x);
       return v;
    }

    /**
     * ""
     * @param v     ""
     * @param n     ""
     * @return      Vector containing values
     */
    private Point2D setY(Point2D v, double n)  {
        double y  = v.getY();    // Stores the Y value of the Point2D vector
        v = new Point2D(y, n);
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
    private Point2D Cohesion(List<Drone> Drones)  {

        Point2D pc = new Point2D(0, 0);          // perceived center, instantiated.
        int total = 0;
        for (Drone neighbour : Drones) {             // For each drone in Drones LinkedList

            Point droneLoc = new Point(this.location.getX(), this.location.getY());
            Point neighbourLoc = new Point(neighbour.location.getX(), neighbour.location.getY());
            double d = droneLoc.distanceTo(neighbourLoc);

            if (neighbour != this && d < droneFlockRadius) {

                pc = pc.add(neighbour.location);
                total++;
            }
        }

        if(total < 0)   {
            pc = Point2D_Div(pc, total);
            pc = pc.subtract(this.location);

            pc = pc.subtract(this.velocity);
        }
        System.out.println("Cohesion:\t" + "\tX: " + new DecimalFormat("0.00") .format(pc.getX()) + "    Y:" + new DecimalFormat("0.00") .format(pc.getY()));
        return pc;  // new perceived center
    }

    /**
     * Separation function
     * Keeps a minimum distance between drones when they're moving around. Ensures they don't collide.
     *
     * @return return perceived center
     */
    private Point2D Separation(List<Drone> Drones) {

        Point2D pc = new Point2D(0, 0);
        int total = 0;
        for (Drone neighbour : Drones) {

            Point droneLoc = new Point(this.location.getX(), this.location.getY());
            Point neighbourLoc = new Point(neighbour.location.getX(), neighbour.location.getY());
            double d = droneLoc.distanceTo(neighbourLoc);

            if (neighbour != this && d < droneFlockRadius) {

                    Point2D diff = this.location.subtract(neighbour.location);
                    diff = diff.multiply(1/d);
                    pc = pc.add(diff);
                    total++;
            }
            if(total > 0)   {
                pc = Point2D_Div(pc, total);
                pc = pc.subtract(this.velocity);

            }
        }

        System.out.println("\nSeparation:\t" + "\tX: " + new DecimalFormat("0.00").format(pc.getX()) + "    Y:" + new DecimalFormat("0.00").format(pc.getY()));
        return pc;
    }

    /**
     * Alignment function
     * Ensures velocity amongst drones is close to or matches velocity or nearby drones
     *
     * @return perceived velocity value
     */
    private Point2D Alignment(List<Drone> Drones) {

        int total = 0;
        double flockRadius = droneFlockRadius;
        Point2D pv = new Point2D(0, 0); // Perceived Velocity

        for(Drone neighbour : Drones)   {
            Point2D d = new Point2D(0,0);
            double dd = (neighbour.location.getX() - this.location.getX()) + (neighbour.location.getY() - this.location.getY());

            if(neighbour != this && dd < droneFlockRadius) {
                pv = pv.add(neighbour.velocity);
                pv = pv.subtract(this.velocity);
                total++;
            }
        }
        System.out.println("Alignment:\t" + "\tX: " + new DecimalFormat("0.00") .format(pv.getX()) + "    Y:" + new DecimalFormat("0.00") .format(pv.getY()));
        return pv;
    }



    public Point2D edges(Point2D loc)    {

        Point Loc = new Point(loc.getX(), loc.getY());
        int menuHeight = 0, tbHeight = 80;
        if (Loc.getX() >= sceneWidth)  {
            Loc.setX(0+droneRadius);
            setFill(randomColour());
        } else if (loc.getX() <= 0)    {
            Loc.setX(sceneWidth-droneRadius);
            setFill(randomColour());
        }

        if (loc.getY() >= sceneHeight - tbHeight)  {        // Refers to bottom of screen
            Loc.setY(0+droneRadius+menuHeight);
            setFill(randomColour());
        } else if (loc.getY()  <= 0 + menuHeight)    {        // Refers to top of screen
            Loc.setY(sceneHeight-droneRadius-tbHeight);
            setFill(randomColour());
        }
        loc = new Point2D(Loc.getX(), Loc.getY());
        return loc;
    }

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
    /*public List<Drone> flock(LinkedList)  {
        List<Drone> flock = new LinkedList<Drone>();

            for(Drone drone : Drones)   {

                if(isInFlock(drone.location))   {

                    flock.add(drone);
                }
            }
            return flock;
    }*/

    public void flock (List<Drone> Drones)    {
        Point2D cohesion = this.Cohesion(Drones);
        Point2D seperation = this.Separation(Drones);
        Point2D alignment = this.Alignment(Drones);

        this.acceleration = alignment;
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


    public void createPredator()     {
        createDrone();
    }

    public static double magnitude(int vec[]) {

        double mag =0;
        for (int n=0;n<vec.length;n++)
            mag  +=  Math.pow(vec[n],2);

        return Math.sqrt(mag);
    }
}