package SimulatorForDrones;


public class Point {
        private double x;
        private double y;

        public Point() {
            this.x = 0;
            this.y = 0;
        }

        public Point (Point v) {
            this.x = v.getX();
            this.y = v.getY();
        }

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() { return this.x; }
        public double getY() { return this.y; }

        public void setX(double x) { this.x = x; }
        public void setY(double y) { this.y = y; }

        public double magnitude() {
            return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
        }

        public Point normalize() {
            double mag = this.magnitude();
            if (mag > 0) {
                this.divide(mag);
            }
            return this;
        }

        public Point add(Point v) {
            this.x += v.getX();
            this.y += v.getY();
            return this;
        }

        public Point add(Point v, Point w) {
            return new Point(v.getX() + w.getX(), v.getY() + w.getY());
        }

        public Point subtract(Point v) {
            this.x -= v.getX();
            this.y -= v.getY();
            return this;
        }

        public Point subtract(Point v, Point w) {
            return new Point(v.getX() - w.getX(), v.getY() - w.getY());
        }

        public Point divide(double d) {
            this.x /= d;
            this.y /= d;
            return this;
        }

        public Point multiply(double d) {
            this.x *= d;
            this.y *= d;
            return this;
        }

        public double distanceTo(Point v)
        {
            double dx2 = Math.pow(v.x - this.x, 2);
            double dy2 = Math.pow(v.y - this.y, 2);
            return Math.sqrt(dx2 + dy2);
        }

        public double angleInDegrees() {
            Point v = new Point(0,1);
            double angleRadians = Math.acos(this.dotProduct(v) / (this.magnitude() * v.magnitude()));
            if (this.getX() >= 0) angleRadians *= -1;
            return angleRadians * (180 / Math.PI);
        }

        public double dotProduct(Point v) {
            return this.getX() * v.getX() + this.getY() * v.getY();
        }

        public Point limit(double maxValue) {
            if (this.magnitude() > maxValue) {
                return this.normalize().multiply(maxValue);
            }
            return this;
        }

        @Override
        public Point clone(){
            return new Point(this.x, this.y);
        }
}
