package SimulatorForDrones;

public class Point
{
    private double x;
    private double y;

    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double getX() { return this.x; }
    public double getY() { return this.y; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public double distanceTo(Point p)
    {
        double dx2 = Math.pow(p.x - this.x, 2);
        double dy2 = Math.pow(p.y - this.y, 2);
        return Math.sqrt(dx2 + dy2);
    }
}