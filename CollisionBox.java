import java.awt.*;

public class CollisionBox implements Cloneable {
    private final Circle[] circles;
    private Point offset = new Point(0, 0);

    CollisionBox(Circle[] circles) {
        this.circles = circles;
    }

    public void setOffset(Point offset) {
        this.offset = offset;
    }

    public boolean collideWith(CollisionBox other) {
        for (int i = 0; i < circles.length; i++) {
            for (int j = i; j < other.circles.length; j++) {
                Circle circle1 = circles[i].clone();
                circle1.translate(offset);
                Circle circle2 = other.circles[j].clone();
                circle2.translate(other.offset);
                if (circle1.intersect(circle2)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public CollisionBox clone() throws CloneNotSupportedException {
        CollisionBox newBox = (CollisionBox) super.clone();
        newBox.offset = (Point) offset.clone();

        return newBox;
    }
}
