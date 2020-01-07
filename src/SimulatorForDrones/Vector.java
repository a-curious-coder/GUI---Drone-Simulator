package SimulatorForDrones;

public class Vector {

    public int x;
    public int y;

    public Vector() {}

    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Vector vector) {
        this.x = vector.x;
        this.y = vector.y;
    }

    public void add(Vector... vectors) {

        for (Vector vector : vectors) {

            this.x += vector.x;
            this.y += vector.y;
        }
    }

    public void subtract(Vector... vectors) {

        for (Vector vector : vectors) {

            this.x -= vector.x;
            this.y -= vector.y;
        }
    }

    public void multiply(double number) {

        this.x *= number;
        this.y *= number;
    }

    public void divide(double number) {

        this.x /= number;
        this.y /= number;
    }

    public double length() {

        return Math.sqrt(this.x * this.x + this.y * this.y);
    }
}
