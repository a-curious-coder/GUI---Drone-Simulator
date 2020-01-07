package SimulatorForDrones;

// JavaFX Libraries
import javafx.scene.paint.Color;  // Colour library
import javafx.scene.shape.Circle; // Imports Circle library and allows me to represent a drone with the circle.

// Java Libraries
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Drone extends Circle { // Drone extends circle attributes - represented by circle.

    public static List<Drone> Drones = new ArrayList<>();   // ArrayList to hold all 'Drone' objects - Group of Drones

    private static int nDrones = 20;
    private static int radiusLimit = 10;
    private static int droneFlockRadius = 10;
    private final int panelWidth = Settings.SCENE_WIDTH;
    private final int panelHeight = Settings.SCENE_HEIGHT;
    public Color color;

    private Vector v1 = Cohesion(), v2 = Separation(), v3 = Alignment();
    private Vector location;                                // X, Y coords for drone
    private Vector velocity;                                // ... unsure
    private Vector locationToDraw;
    private Vector acceleration;                            // Acceleration = Force / Mass

    private double mass = 10; // Creates mass variable
    private double maxSpeed = Settings.DRONE_MAX_SPEED; // Sets maxspeed from settings


    /**
     * Drone constructor
     *
     */
    public Drone(int pWidth, int pHeight) {
        Random rnd = new Random();
        this.panelWidth = pWidth;
        this.panelHeight = pHeight;
        //double x = rnd.nextDouble() * panelWidth;  // x coord
        //double y = rnd.nextDouble() * panelHeight; // y coord
        this.location.x = rnd.nextInt() * panelWidth; // location vector within this constructor set to location variable outside of constructor's value.
        this.location.y = rnd.nextInt() * panelHeight;
        //this.acceleration = acceleration;
        //this.mass = mass;
        //this.color = new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        // initialize view depending on mass
        setRadius(this.mass); // Sets radius of circle (Drone)
        setStroke(Color.GRAY); // Set circle colour to Gray
        setFill(Color.BLUE); // Set
    }

    /**
     * MoveAllDrones function - Moves drones once per call
     *
     */
    public void MoveAllDrones()   {
        velocity.add(Cohesion(), Separation(), Alignment());
        //limitVelocity();
        locationToDraw.add(velocity);

        if (locationToDraw.x > panelWidth) {
            locationToDraw.x = 0;
        }
        if (locationToDraw.x < 0) {
            locationToDraw.x = panelWidth;
        }
        if (locationToDraw.y > panelHeight) {
            locationToDraw.y = 0;
        }
        if (locationToDraw.y < 0) {
            locationToDraw.y = panelHeight;
        }
    }

    /**
     * Cohesion function
     * Searches for a perceived center in a 'flock' of drones.
     * @return
     */
    public Vector Cohesion()  {

        Vector pc = new Vector(0, 0); // perceived center, instantiated.

        for (Drone drone : Drones)    {                 // For each drone in Drones ArrayList

            if (!drone.equals(this)) {                  // if Drone is NOT equal to any other drone

                pc.add(drone.location);
            }
        }
        pc.divide(Drones.size() - 1);

        Vector nPC = new Vector(pc); // nPC is new perceived center
        nPC.subtract(this.location);
        nPC.divide(100);

        return nPC;  // new perceived center
    }

    /**
     * Separation function
     * Keeps a minimum distance between drones when they're moving around. Ensures they don't collide.
     *
     * @return
     */
    public Vector Separation() {

        Vector pc = new Vector(0, 0);

        for (Drone drone : Drones)  {

            if (!drone.equals(this)) {                  // if Drone is NOT equal to any other drone

                if(isClose(drone.location)) {

                    Vector sub = new Vector(drone.location);
                    sub.subtract(location);
                    pc.subtract(sub);
                }
            }
        }

        return pc;
    }

    /**
     * Alignment function
     * Ensures velocity amongst drones is close to or matches velocity or nearby drones
     *
     * @return
     */
    public Vector Alignment() {

        Vector pv = new Vector(0, 0);

        for(Drone drone : Drones)   {

            if(!drone.equals(this)) {

                pv.add(drone.velocity);
            }
        }
        pv.divide(Drones.size() - 1);
        pv.subtract(velocity);
        pv.divide(8);

        return pv;
    }

    public static void initDrones(int pWidth, int pHeight)  {
        for (int i = 0; i < nDrones; i++) {
            Drones.add(new Drone(pWidth, pHeight));
        }
    }

    public List<Drone> Drones() {
        List<Drone> flock = new LinkedList<Drone>();
        for (Drone drone : Drones) {
            if (isInDroneFlock(drone.location)) {
                Drones.add(drone);
            }
        }
        return Drones;
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