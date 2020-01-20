package SimulatorForDrones;

// JavaFX Libraries
import javafx.scene.paint.Color;  // Colour library
import javafx.scene.shape.Circle; // Imports Circle library and allows me to represent a drone with the circle.
import javafx.scene.transform.Rotate;

// Java Libraries
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
    private static int droneFlockRadius = 100;                                 // Flock Radius (size)
    private static double minVelocity = -5, maxVelocity = 5;
    private double maxForce = Settings.DRONE_MAX_FORCE;
    private double maxSpeed = Settings.DRONE_MAX_SPEED;

    private double neighbourRadius;
    private double desiredSeparationRadius;
    private double obstacleAvoidanceRadius;
    private Rotate rotationTransform;
    private int cohesionTotal = 0;
    private int separationTotal = 0;
    private int alignmentTotal = 0;
    public static double COHESION_WEIGHT = Settings.COHESION_WEIGHT;
    public static double SEPARATION_WEIGHT = Settings.SEPARATION_WEIGHT;
    public static double ALIGNMENT_WEIGHT = Settings.ALIGNMENT_WEIGHT;
    public Point location;                                                 // Stores X and Y coordinates for drone
    private Point velocity;                                                 // Determines speed of drone - helps with next location

    /**
     *
     * @param x     X coordinate for drone spawn
     * @param y     Y coordinate for drone spawn
     * @param v     V is the velocity of the drone
     *
     */
    public Drone(int droneUniqueID, double x, double y, Point v) {

        this.location = new Point(x, y);
        this.velocity = v;               // Set velocity

        // Generate new and random magnitude for velocity
        this.velocity = this.velocity.normalize();
        this.velocity = this.velocity.multiply(rnd.nextDouble() * 4);

        this.desiredSeparationRadius = droneRadius * 1;
        this.obstacleAvoidanceRadius = droneRadius * 3;
        this.neighbourRadius = droneRadius * 2;

        setRadius(droneRadius);                     // Sets drone radius (mass/size)
        setStroke(color);                      // Sets drone edge colour
        setFill(color.deriveColor(1, 1, 1, 0.2));   // Sets drone internal colour.

    }


    public static Drone createDrone()   {
        int id = Drones.size() + 1;


        Point loc = setRandomLocation();
        double x = loc.getX();
        double y = loc.getY();

        double vX = (Math.random() * ((maxVelocity - minVelocity) + 1)) + minVelocity;
        double vY = (Math.random() * ((maxVelocity - minVelocity) + 1)) + minVelocity;
        Point velocity = new Point(vX, vY);

        Drone drone = new Drone(id, x, y, velocity);
        Drones.add(drone);
        return drone;
        //System.out.println("Drone: " + id + "\t\tX: " + loc.getX() + "\tY: " + loc.getY());
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

            Point rule1 = this.Cohesion();
            Point rule2 = this.Separation();
            Point rule3 = this.Alignment();

            // Adds values to velocity vector

            edges();
            //limitVelocity();

            Point changeInVelocity = new Point (0, 0);
            //changeInVelocity = changeInVelocity.add(rule2).add(rule3);
            //double angle = this.getVelocity().angleInDegrees();
            //this.getRotationTransform().setAngle(angle);
            this.setVelocity(this.getVelocity().add(changeInVelocity).limit(this.getMaxSpeed()));       // Limits maxSpeed
            this.setLocation(this.getLocation().add(this.getVelocity()));

            //constrainPosition(this);    // Sets constraints to how far out the drones can move - cannot move outside scene
    }

    /**
     * updates the user interface - location x and y coordinates update for the drones - allows movement.
     */
    void updateUI() {

        this.setLayoutX(this.getLocation().getX());
        this.setLayoutY(this.getLocation().getY());

    }


    /**
     * Cohesion function
     * Searches for a perceived center in a 'flock' of drones.
     * @return return newly calculated perceived center
     */
    private Point Cohesion() {

        Point pc = new Point(0, 0);          // perceived center, instantiated.
        cohesionTotal = 0;

        for(Drone drone : Drones) {

            for (Drone neighbour : Drones) {

                if (neighbour.equals(drone)) continue;

                double distance = drone.getLocation().distanceTo(neighbour.getLocation());

                if (distance < drone.getNeighbourRadius()) {

                    pc.add(neighbour.getLocation());
                    cohesionTotal++;
                }
            }

            if (cohesionTotal > 0) {

                pc = pc.divide(cohesionTotal);
                pc = drone.steer(pc);
            }
        }
        pc = pc.multiply(COHESION_WEIGHT);
        //System.out.println("Cohesion:\t[" + pc.getX() + ", " + pc.getY() + "]");
        return pc;  // new perceived center
    }
    /*for (Drone neighbour : Drones) {             // For each drone in Drones LinkedList

        if (neighbour.equals(drone)) continue;

        // pc + neighbour location for the purpose of steering toward this location
        pc = pc.add(neighbour.location);
        cohesionTotal++;
    }*/
    /**
     * Separation function
     * Keeps a minimum distance between drones when they're moving around. Ensures they don't collide.
     *
     * @return return perceived center
     */
    private Point Separation() {

        Point pc = new Point(0, 0);
        separationTotal = 0;

        for (Drone drone : Drones) {

            for (Drone neighbour : Drones) {

                if (neighbour.equals(drone)) continue;

                double distance = drone.getLocation().distanceTo(neighbour.getLocation());

                if (distance <= drone.getDesiredSeparationRadius()) {

                    pc = pc.add(pc.subtract(drone.getLocation(), neighbour.getLocation()).normalize().divide(distance));
                    separationTotal++;
                }
            }

            if (separationTotal > 0) {
                // divide pc by total
                pc = pc.divide(separationTotal);
            }

            pc.multiply(SEPARATION_WEIGHT);
        }
        //System.out.println("\nSeparation:\t[" + pc.getX() + ", " + pc.getY() + "]");
        return pc;
    }


    /**
     * Alignment function
     * Ensures velocity amongst drones is close to or matches velocity or nearby drones
     *
     * @return perceived velocity value
     */
    private Point Alignment() {

        Point pv = new Point(0, 0); // Perceived Velocity
        alignmentTotal = 0;

        for (Drone drone : Drones) {

            for (Drone neighbour : Drones) {

                if (neighbour.equals(drone)) continue;

                double distance = drone.getLocation().distanceTo(drone.getLocation());

                if (distance < drone.getNeighbourRadius()) {

                    pv = pv.add(neighbour.getVelocity());
                    alignmentTotal++;
                }
            }

            if (alignmentTotal > 0) {

                pv = pv.divide(alignmentTotal).limit(drone.getMaxForce());
            }
        }

        pv = pv.multiply(ALIGNMENT_WEIGHT);
        return pv;
        //System.out.println("\nAlignment:\t[" + pv.getX() + ", " + pv.getY() + "]");
    }


   /* private Point Scatter () {
        for (Drone drone : Drones) {
            Vector scatter = new Vector();
            for (Drone neighbour : Drones) {

                if (d < neighbour.getNeighbourRadius()) {

                    if (otherBoid instanceof Predator && !(currentBoid instanceof Predator)) {

                        scatter.add(Vector.subtract(currentBoid.getPosition(), otherBoid.getPosition()).normalize().divide(distance));
                    }

                }

            }
        }
    }*/




    private Point bound_position(Drone drone)    {
        double xMin = droneRadius, xMax = sceneWidth, yMin = droneRadius, yMax = sceneHeight;
        int pX = 10, nX = -10, pY = 10, nY = -10;

        Point v = new Point(0, 0);

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
     * There are some integer variables that cater for the toolbars, menus, etc. in my GUI. This is so they can't
     * go through them.
     *
     * @return      new location
     */
    public Point edges()    {

        Point loc = new Point(this.location.getX(), this.location.getY());
        double menuBarHeight = Main.getMenuBarHeight(),
                controlBarHeight = Main.getControlGridHeight();

        if (loc.getX() >= sceneWidth)  {

            loc.setX(0+droneRadius);
        } else if (this.location.getX() <= 0)    {

            loc.setX(sceneWidth-droneRadius);
        }

        if (this.location.getY() >= sceneHeight - controlBarHeight)  {        // Refers to bottom of screen

            loc.setY(0 + droneRadius + menuBarHeight );

        } else if (this.location.getY()  <= 0 + menuBarHeight)    {        // Refers to top of screen

            loc.setY(sceneHeight - droneRadius - controlBarHeight);
        }
        this.location = new Point(loc.getX(), loc.getY());
        return this.location;
    }

    /**
     * setRandomLocation function - sets the location of a drone to random value when constructor is called.
     * No two drones will have same position.
     *
     */
    public static Point setRandomLocation()    {

        Point loc = new Point(rnd.nextInt((int)sceneWidth) + droneRadius, rnd.nextInt((int)sceneHeight) + droneRadius);   // Instantiate location


        for (Drone neighbour : Drones) {

            if (loc.getX() == neighbour.location.getX() + droneRadius && loc.getY() == neighbour.location.getY() + droneRadius) {     // if location.getX() and location.getY() are equal to any other drones getX() and getY() coords

                double x = rnd.nextInt((int)sceneWidth) + droneRadius;
                double y = rnd.nextInt((int)sceneHeight) + droneRadius;

                loc = new Point(x, y);
                // Re-roll random x and y coordinate
            }
        }

        return loc;
    }

    /**
     * Returns a random colour
     */
    public static Color randomColour() {

        int range = 220;
        return Color.rgb((int) (rnd.nextDouble() * range), (int) (rnd.nextDouble() * range), (int) (rnd.nextDouble() * range));
    }

    /**
     * limits the velocity for each drone - ensures they don't gain infinite speed.
     */
    private void limitVelocity() {
        if (velocity.magnitude() > maxVelocity) {
            velocity =  velocity.multiply(1d/(velocity.magnitude())).multiply(maxVelocity);
        }
    }


    private void constrainPosition(Drone drone) {

        double xMin = droneRadius;
        double xMax = sceneWidth;
        double yMin = droneRadius;
        double yMax = sceneHeight;

        double x = drone.location.getX();
        double y = drone.location.getY();
        double vx = drone.velocity.getX();
        double vy = drone.velocity.getY();

        if (x < xMin) {                 // if getX() is less than xMin

            x = xMin;                   // getX() = xMin
            vx = velocity.getX();                     // vx = v
        } else if (x > xMax) {            // if getX() is more than xMax

            x = xMax;                   // getX() == xMax
            vx = -velocity.getX();                    // vx = -v
        }

        if (y < yMin) {                 // If getY() coordinate is less than the getY() Min (intersection another drones space I assume)

            y = yMin;                   // getY() becomes yMin
            vy = velocity.getY();                     // vy becomes v
        } else if (y > yMax) {            // If getY() coordinate is more than the getY() Max then

            y = yMax;                   // getY() coordinate reset to yMax
            vy = -velocity.getY();                    // velocity getY() = -v
        }

        // TODO: Find less power consuming solution: find out how to modify the vector directly or create own Point class
        location = new Point(x, y);
        velocity = new Point(vx, vy);

    }


    /**
     * Checks whether drone is within flock area
     * @param loc   location of drone
     * @return  ??
     */
    private boolean isInFlock(Point loc)  {
        Point range = new Point(location.getX(), location.getY());
        range.subtract(loc);
        return range.magnitude() < droneFlockRadius;
    }

    private boolean isClose(Point location) {
        Point range = new Point(location.getX(), location.getY());
        range = range.subtract(location);                                             // Subtracts location vector passed from range
        //System.out.println("range.magnitude() < radiusLimit\t" + range.magnitude() + " < " + radiusLimit);
        if(range.magnitude() < droneFlockRadius) {
            //System.out.println("true");
            return true;
        }
        return range.magnitude() < droneFlockRadius;
    }

    private Point Point_Div(Point p, double A)  {
        Point v = new Point(0,0);

        setX(v, p.getX() / A);
        setY(v, p.getY() / A);

        return v;
    }


    public void createPredator()     {
        createDrone();
        setRadius(droneRadius*3);
        setFill(Color.RED);
    }

    /**
     * Due to limitations of Point library, you cannot individually set X and Y values, only both.
     * The issue here is that if y is holding a value and you want to only set X, you need to reset the Y value.
     * This function stores the Y value and sets x whilst passing the stored y value - maintaining all values necessary
     *
     * @param v     The vector being passed in
     * @param n     The number that's going to be used in setting the x value
     * @return      Vector containing values
     */

    private Point setX(Point v, double n)  {
        double x  = v.getX();    // Stores the Y value of the Point vector
        v = new Point(n, x);
        return v;
    }

    /**
     * ""
     * @param v     ""
     * @param n     ""
     * @return      Vector containing values
     */
    private Point setY(Point v, double n)  {
        double y  = v.getY();    // Stores the Y value of the Point vector
        v = new Point(y, n);
        return v;
    }

    //---------------------------------------------
    //                  Getters
    //---------------------------------------------

    public Point getLocation()  {
        return location;
    }

    public void setLocation(Point location)  {
        this.location = location;
    }
    
    public Point getVelocity()  {
        return velocity;
    }

    public void setVelocity(Point velocity) {
        this.velocity = velocity;
    }

    public double getDesiredSeparationRadius()   {
        return desiredSeparationRadius;
    }

    public double getMaxForce() {
        return maxForce;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getNeighbourRadius() {
        return neighbourRadius;
    }

    public Rotate getRotationTransform() {
        return this.rotationTransform;
    }

    public Point steer(Point target) {
        Point desired = new Point(0,0);
        desired = desired.subtract(target, this.getLocation());

        if (desired.magnitude() > 0) {
        // Sets the magnitude to maxSpeed      
            desired.normalize();
            desired.multiply(maxSpeed);
            return desired.subtract(this.getVelocity()).limit(maxForce);
        }
        return new Point();
    }
}