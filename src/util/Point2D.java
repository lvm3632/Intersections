package util;

import java.util.Objects;

public class Point2D implements Comparable<Point2D>{
    public double x;
    public double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o)
    {
        if(this==o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Point2D point2D = (Point2D) o;
        return Double.compare(point2D.x, x) == 0 &&
                Double.compare(point2D.y,y)==0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(x,y);
    }

    @Override
    public int compareTo(Point2D o) {
        if (this.y != o.y) {
            return java.lang.Double.compare(o.y, this.y);
        } else {
            return java.lang.Double.compare(this.x, o.x);
        }
    }

    public boolean equals(Point2D p) {
        return this.x == p.x && this.y == p.y;
    }

    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
}
