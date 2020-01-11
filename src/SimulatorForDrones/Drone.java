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

    public final static List<Drone> Drones = new LinkedList<Drone>();   // ArrayList to hold all 'Drone' objects - Group of Drones

    private static int nDrones = 20;                        // Number of drones
    private static int radiusLimit = 10;                    // ----
    private static int velocityLimit = 15;
    private static int droneFlockRadius = 10;               // Flock Radius (size)
    private static int droneCounter = 0;
    private final int panelWidth;                           // ??
    private final int panelHeight;                          // ??
    public Color color = Color.BLACK;

    private Vector v1 = Cohesion(),
                   v2 = Separation(),
                   v3 = Alignment();
    private Vector location;                                // X, Y coords for drone
    private Vector locationToDraw;
    private Vector velocity;                                // ... unsure

    private int droneNumber;
    private double mass = 10; // Creates mass variable
    //private double maxSpeed = Settings.DRONE_MAX_SPEED; // Sets maxspeed from settings


    /**
     * 2 Drone constructors
     * Called dependant on what parameters are given upon request.
     */
    public Drone(int pWidth, int pHeight) {

        this.panelWidth = pWidth;                                       // panelWidth value is changed from default null to pWidth value
        this.panelHeight = pHeight;                                     // "" with height
        setRandomLocation();                                            // Sets random location for drone within window size.
        this.locationToDraw = new Vector();                             // Instantiate locationToDraw Vector
        this.droneNumber = droneCounter++;                              // Iterate droneNumber for printing specific drone no.

        System.out.println("Created drone: " + this.droneNumber);       // Prints drone number to console.
    }

    public Drone (Drone drone)  {

        Random rnd = new Random();                                      // Instantiate Random

        this.color = drone.color;                                       // Sets drone color.
        this.panelWidth = drone.panelWidth;                             // panelWidth value not set, set to drone.panelWidth
        this.panelHeight = drone.panelHeight;                           // "" with height
        this.velocity = new Vector(drone.velocity);                     // "" velocity
        this.location = new Vector(drone.location);                     // this.location equal to Vector (drone.location)
        this.locationToDraw = new Vector(drone.locationToDraw);         // "" locationToDraw
    }

    public static void initialiseDrones(int pWidth, int pHeight)  {

        for (int i = 0; i < nDrones; i++) {

            Drones.add(new Drone(pWidth, pHeight));
            System.out.println("Drone: " + i + "\t pWidth/pHeight: " + pWidth + " " + pHeight);
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
        location.x = rnd.nextInt(panelWidth);                           // location x element of vector set to random value * panel width
        location.y = rnd.nextInt(panelHeight);                          // "" y with height

        while(true) {

            for (Drone drone : Drones) {

                if (location.x == drone.location.x && location.y == drone.location.y) {     // if location.x and location.y are equal to any other drones x and y coords

                    location.x = rnd.nextInt(panelWidth);               // Re-roll random x coordinate
                    location.y = rnd.nextInt(panelHeight);              // Re-roll random y coordinate
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
    public void MoveAllDrones()   {

        velocity.add(Cohesion(), Separation(), Alignment());
        limitVelocity();
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

    public LinkedList<Drone> draw(LinkedList<Drone> Drones) {

        return Drones;
    }

    /**
     * Cohesion function
     * Searches for a perceived center in a 'flock' of drones.
     * @return return newly calculated perceived center
     */
    private Vector Cohesion()  {

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
     * @return return perceived center
     */
    private Vector Separation() {

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
     * @return perceived velocity value
     */
    private Vector Alignment() {

        Vector pv = new Vector(0, 0); // Perceived Velocity

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

    private void limitVelocity() {
        if (velocity.length() > velocityLimit) {

            velocity.divide(velocity.length());
            velocity.multiply(velocityLimit);
        }
    }


    /*public List<Drone> flock() {

        List<Drone> flock = new LinkedList<Drone>();

        for (Drone drone : Drones) {

            if (isInDroneFlock(drone.location)) {

                flock.add(drone);
            }
        }
        return flock;
    }*/

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