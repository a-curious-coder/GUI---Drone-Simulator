package SimulatorForDrones;

// JavaFX Libraries
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;  // Colour library
import javafx.scene.shape.Circle; // Imports Circle library and allows me to represent a drone with the circle.
import javafx.scene.transform.Rotate;

// Java Libraries
import java.util.*;
import java.util.List;

public class Drone extends Circle   { // Drone extends circle attributes - represented by circle.

    public static List <Drone> Drones = new LinkedList<Drone>();   // LinkedList to hold all 'Drone' objects - Group of Drones
    public static List<Circle> Obstacles = new LinkedList<Circle>();
    public static ArrayList<Predator> Predators = new ArrayList<Predator>();

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
    private static double maxSpeed = Settings.DRONE_MAX_SPEED;

    private double neighbourRadius;
    private double desiredSeparationRadius;
    private double obstacleAvoidanceRadius;
    public static double COHESION_WEIGHT = Settings.COHESION_WEIGHT;
    public static double SEPARATION_WEIGHT = Settings.SEPARATION_WEIGHT;
    public static double ALIGNMENT_WEIGHT = Settings.ALIGNMENT_WEIGHT;
    public static double SCATTER_WEIGHT = Settings.SCATTER_WEIGHT;
    public Point location;                                                 // Stores X and Y coordinates for drone
    private Point velocity;                                                 // Determines speed of drone - helps with next location

    /**
     *
     * @param x     X coordinate for drone spawn
     * @param y     Y coordinate for drone spawn
     */
    public Drone(double x, double y) {

        this.location = new Point(x, y);

        // Generate new and random magnitude for velocity
        this.velocity = new Point((Math.random() * (maxVelocity - minVelocity + 1)) + minVelocity, (Math.random() * ((maxVelocity - minVelocity) + 1)) + minVelocity);

        this.neighbourRadius = droneRadius * 10;
        this.desiredSeparationRadius = droneRadius * 3;
        this.obstacleAvoidanceRadius = droneRadius * 6;

        setRadius(droneRadius);                     // Sets drone radius (mass/size)
        setStroke(color);                      // Sets drone edge colour
        setFill(color.deriveColor(1, 1, 2, 0.5));   // Sets drone internal colour.

    }

    /**
     * Creates drone with random location
     * Adds drone to LinkedList
     * @return      drone
     */
    public static Drone createDrone()   {

        Drone drone = new Drone(setRandomLocation().getX(), setRandomLocation().getY());
        Drones.add(drone);
        return drone;
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
    public static void moveDrones()   {
            for(Drone drone : Drones) {

                Point cohesion = Cohesion(drone);
                Point separation = Separation(drone);
                Point alignment = Alignment(drone);
                Point avoidance = avoidance(drone);
                Point scatter = scatter(drone);

                edges(drone);
                limitVelocity(drone);

                Point changeInVelocity = new Point(0, 0);
                changeInVelocity = changeInVelocity
                        .add(cohesion)
                        .add(separation)
                        .add(alignment)
                        .add(avoidance)
                        .add(scatter);

                drone.velocity = drone.velocity.add(changeInVelocity).limit(getMaxSpeed());     // Limits maxSpeed
                drone.location = drone.location.add(drone.velocity);                            // Adds velocity to location
            }
        //constrainPosition(this);    // Sets constraints to how far out the drones can move - cannot move outside scene
    }

    /**
     * updates the user interface - location x and y coordinates update for the drones - allows movement.
     */
    void updateUI() {

        setLayoutX(this.location.getX());
        setLayoutY(this.location.getY());

    }


    /**
     * Cohesion function
     * Searches for a perceived center in a 'flock' of drones.
     * @return   the calculated 'perceived center' of a group
     */
    private static Point Cohesion(Drone drone) {

        Point pc = new Point(0, 0);          // perceived center, instantiated.
        int cohesionTotal = 0;

            for (Drone neighbour : Drones) {

                if (neighbour.equals(drone)) continue;

                    double distance = drone.getLocation().distanceTo(neighbour.getLocation());

                    if (distance < drone.getNeighbourRadius()) {

                        // pc + neighbour location for the purpose of steering toward this location
                        pc = pc.add(neighbour.getLocation());
                        cohesionTotal++;
                    }
            }

            if (cohesionTotal > 0) {

                pc = pc.divide(cohesionTotal);
                pc = drone.steer(pc);
            }

        pc = pc.multiply(COHESION_WEIGHT);
        //System.out.println("Cohesion:\t[" + pc.getX() + ", " + pc.getY() + "]");
        return pc ;//= pc.subtract(this.getLocation());  // new perceived center vector

    }

    /**
     * Separation function
     * Keeps a minimum distance between drones when they're moving around. Ensures they don't collide.
     *
     * @return return perceived center
     */
    private static Point Separation(Drone drone) {

            Point separation = new Point();

            int separationTotal = 0;

            for (Drone neighbour : Drones) {

                if (neighbour.equals(drone)) continue;

                double distance = drone.getLocation().distanceTo(neighbour.getLocation());

                if (distance <= drone.getDesiredSeparationRadius()) {

                    separation = separation.add(separation.subtract(drone.getLocation(), neighbour.getLocation()).normalize().divide(distance));

                    separationTotal++;
                }


            }


            if (separationTotal > 0) {

                separation = separation.divide(separationTotal);
            }

            separation = separation.multiply(SEPARATION_WEIGHT);
            //System.out.println("\nSeparation:\t[" + pc.getX() + ", " + pc.getY() + "]");
        return separation;
        }



    /**
     * Alignment function
     * Ensures velocity amongst drones is close to or matches velocity or nearby drones
     *
     * @return perceived velocity value
     */
    private static Point Alignment(Drone drone) {

        Point pv = new Point(0, 0); // Perceived Velocity
        int alignmentTotal = 0;


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

        pv = pv.multiply(ALIGNMENT_WEIGHT);

        //System.out.println("\nAlignment:\t[" + pv.getX() + ", " + pv.getY() + "]");
        return pv;
    }

    public static Point avoidance (Drone drone)  {

        Point avoidance = new Point(0, 0);
        double closestObstacleDistance = Integer.MAX_VALUE;

        for (Circle obstacle : Obstacles) {

            Point obstaclePosition = new Point(obstacle.getLayoutX(), obstacle.getLayoutY());
            double distance = drone.getLocation().distanceTo(obstaclePosition);
            if (distance < drone.getObstacleAvoidanceRadius()) {

                Point collisionVector = new Point(drone.getVelocity()).normalize().multiply(distance);
                if (collisionVector.add(drone.getLocation()).distanceTo(obstaclePosition) <= obstacle.getRadius() + drone.getRadius()) {

                    if (distance < closestObstacleDistance) {

                        closestObstacleDistance = distance;
                        avoidance = new Point(collisionVector);
                        avoidance.subtract(obstaclePosition).normalize();
                    }
                }
            }
        }

        return avoidance;
    }

    public static Point scatter(Drone drone)    {


            Point scatter = new Point();

            for (Drone neighbour : Drones) {

                if (neighbour.equals(drone)) continue;

                double distance = drone.getLocation().distanceTo(neighbour.getLocation());

                if (distance < drone.getNeighbourRadius()) {

                    if (neighbour instanceof Predator && !(drone instanceof Predator)) {

                        scatter = scatter.add(scatter.subtract(drone.getLocation(), neighbour.getLocation()).normalize().divide(distance));
                    }
                }
            }

            scatter = scatter.multiply(SCATTER_WEIGHT);

        return scatter;
    }




    private Point bound_position(Drone drone)    {
        double xMin = droneRadius, xMax = sceneWidth, yMin = droneRadius, yMax = sceneHeight;
        int pX = 10, nX = -10, pY = 10, nY = -10;

        Point v = new Point(0, 0);

        if(drone.location.getX() < xMin)    {
            drone.getLocation().setX(pX);
        } else if (drone.location.getX() > xMax)    {
            drone.getLocation().setX(nX);
        }

        if(drone.location.getY() < yMin)    {
            drone.getLocation().setY(pY);
        } else if (drone.location.getY() > yMax)    {
            drone.getLocation().setY(nY);
        }

        return v;
    }

    /**
     * There are some integer variables that cater for the toolbars, menus, etc. in my GUI. This is so they can't
     * go through them.
     *
     * @return      new location
     */
    public static Point edges(Drone drone)    {

        Point loc = new Point(drone.location.getX(), drone.location.getY());
        double menuBarHeight = Main.getMenuBarHeight(),
                controlBarHeight = Main.getControlGridHeight();

        if (loc.getX() >= sceneWidth - 200)  {

            loc.setX(0+droneRadius);
        } else if (drone.location.getX() <= 0)    {

            loc.setX(sceneWidth-droneRadius);
        }

        if (drone.location.getY() >= sceneHeight - controlBarHeight)  {        // Refers to bottom of screen

            loc.setY(0 + droneRadius + menuBarHeight );

        } else if (drone.location.getY()  <= 0 + menuBarHeight)    {        // Refers to top of screen

            loc.setY(sceneHeight - droneRadius - controlBarHeight);
        }
        drone.location = new Point(loc.getX(), loc.getY());
        return drone.location;
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
    private static void limitVelocity(Drone drone) {
        if (drone.velocity.magnitude() > maxVelocity) {
            drone.velocity =  drone.velocity.multiply(1d/(drone.velocity.magnitude())).multiply(maxVelocity);
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


    public void createPredator()     {
        createDrone();
        setRadius(droneRadius*3);
        setFill(Color.RED);
    }



    //---------------------------------------------
    //             Getters and Setters
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

    public double getObstacleAvoidanceRadius()  {
        return obstacleAvoidanceRadius;
    }
    public double getMaxForce() {
        return maxForce;
    }

    public static double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getNeighbourRadius() {
        return neighbourRadius;
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