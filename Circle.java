import java.awt.*;

public class Circle implements Cloneable{
    private int radius; //圆形半径
    private Point center; //圆心

    public Circle(int radius, int x, int y) {
        this.radius = radius;
        center = new Point(x,y);
    }

    public Point getCenter() {
        return center;
    }

    //平移
    public void translate(Point diff)
    {
        center.translate(diff.x,diff.y);
    }

    // 判断两个圆是否相交
    public boolean intersect(Circle other) {
        return center.distance(other.center) < radius + other.radius;
    }

    @Override
    protected Circle clone() {
        Circle newCircle = null;
        try {
            newCircle = (Circle) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        newCircle.center = (Point) center.clone();
        return newCircle;
    }
}