import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameObject implements Cloneable {
    private double x; // 坐标x
    private double y; // 坐标y
    private double vx;  // 速度x
    private double vy;  // 速度y
    protected int moveCount = 0; //移动计数
    BufferedImage appearance;  //外观
    CollisionBox collisionBox = new CollisionBox(new Circle[]{}); //碰撞箱

    public void init() {
        moveCount = 0;
        x = 0;
        y = 0;
        vx = 0;
        vy = 0;
    }

    public boolean collideWith(GameObject other) {
        return collisionBox.collideWith(other.collisionBox);
    }

    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
        collisionBox.setOffset(getPos());
    }

    public void setPos(Point pos) {
        setPos(pos.x, pos.y);
    }

    public Point getPos() {
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    public void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void setAppearance(BufferedImage img) {
        appearance = img;
    }

    public BufferedImage getAppearance() {
        return appearance;
    }

    public void setCollisionBox(CollisionBox collisionBox) {
        this.collisionBox = collisionBox;
    }

    //以当前速度移动一次
    public void move() {
        x += vx;
        y += vy;
        collisionBox.setOffset(getPos());
        ++moveCount;
    }

    //反向移动一次
    public void moveBack() {
        x -= vx;
        y -= vy;
        collisionBox.setOffset(getPos());
        --moveCount;
    }

    @Override
    protected GameObject clone() throws CloneNotSupportedException {
        GameObject newObject = (GameObject) super.clone();
        newObject.collisionBox = collisionBox.clone();

        return newObject;
    }
}