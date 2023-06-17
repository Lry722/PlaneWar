import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Boss extends Shooter{
    private static BufferedImage appearance;
    private static BufferedImage bullet1Appearance;
    private static BufferedImage bullet2Appearance;
    private static Clip downSound;

    static {
        try {
            appearance = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/boss.png"));
            bullet1Appearance = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/boss bullet.png"));
            bullet2Appearance = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/enemy bullet 2.png"));
            downSound = AudioSystem.getClip();
            downSound.open(AudioSystem.getAudioInputStream(new BufferedInputStream(PlaneWar.class.getClassLoader().getResourceAsStream("sound/boss down.wav"))));
            FloatControl volume = (FloatControl) downSound.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(6f);
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("加载boss资源时出错");
            e.printStackTrace();
        }
    }

    private final Player player;

    public Boss(Player player) {
        this.player = player;
        setAppearance(appearance);
        setCollisionBox(new CollisionBox(new Circle[]{
                new Circle(35,0,12),
                new Circle(13,0,-31),
                new Circle(8,0,-48),
                new Circle(17,45,-2),
                new Circle(17,-45,-2),
                new Circle(8,71,-18),
                new Circle(8,-71,-18),
                new Circle(16,69,-5),
                new Circle(16,-69,-5)
        }));
    }

    @Override
    public void init() {
        super.init();
        setHP(10000);
    }

    @Override
    public void move() {
        if (moveCount <= 20)
            setVelocity(0,-(20 - moveCount));
        else if (moveCount == 180)
            setVelocity(-4,0);

        super.move();
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        if (getHP() == 0)
        {
            downSound.setFramePosition(0);
            downSound.start();
        }
    }

    @Override
    public ArrayList<Bullet> shoot() {
        ArrayList<Bullet> bullets = new ArrayList<>();
        Bullet bullet = new Bullet();
        bullet.setTarget(Bullet.Target.PLAYER);
        bullet.setDamage(20);

        if (this.moveCount < 20)
            return bullets;
        else if (moveCount <= 100)
        {
            if (moveCount - lastShotMoveCount < 10)
                return bullets;
            bullet.setAppearance(bullet2Appearance);
            bullet.setCollisionBox(new CollisionBox(new Circle[]{
                    new Circle(7, 0, 0)
            }));
            double radians = Math.toRadians((moveCount - 20) * 1.5 + 30);
            bullets.add(super.shoot(-42,-12,-Math.cos(radians) * 4,-Math.sin(radians) * 4,bullet));
            bullets.add(super.shoot(42,-12, Math.cos(radians) * 4,-Math.sin(radians) * 4,bullet));
        }
        else if (moveCount <= 180) {
            if (moveCount - lastShotMoveCount < 10)
                return bullets;
            bullet.setAppearance(bullet2Appearance);
            bullet.setCollisionBox(new CollisionBox(new Circle[]{
                    new Circle(7, 0, 0)
            }));
            double radians = Math.toRadians(150 - (moveCount - 100) * 1.5);
            bullets.add(super.shoot(-42,-12,-Math.cos(radians) * 4,-Math.sin(radians) * 4,bullet));
            bullets.add(super.shoot(42,-12,Math.cos(radians) * 4,-Math.sin(radians) * 4,bullet));
        } else {
            switch (shootCount % 9)
            {
                case 0:
                    if (moveCount - lastShotMoveCount < 30)
                        return bullets;
                case 1:
                case 2:
                case 3:
                    if (moveCount - lastShotMoveCount < 20)
                        return bullets;
                    bullet.setAppearance(bullet1Appearance);
                    bullet.setCollisionBox(new CollisionBox(new Circle[]{
                            new Circle(5, 0, -13),
                            new Circle(4, 0, -6)
                    }));
                    bullets.add(super.shoot(-62,-17,0,-4,bullet));
                    bullets.add(super.shoot(62,-17,0,-4,bullet));
                    break;
                case 4:
                    if (moveCount - lastShotMoveCount < 30)
                        return bullets;
                case 5:
                    if (moveCount - lastShotMoveCount < 20)
                        return bullets;
                    bullet.setAppearance(bullet2Appearance);
                    bullet.setCollisionBox(new CollisionBox(new Circle[]{
                            new Circle(7, 0, 0)
                    }));
                    bullets.add(super.shoot(-42, -12, 0, -3, bullet));
                    bullets.add(super.shoot(-42, -12, -2, -3, bullet));
                    bullets.add(super.shoot(-42, -12, 2, -3, bullet));
                    bullets.add(super.shoot(42, -12, 0, -3, bullet));
                    bullets.add(super.shoot(42, -12, -2, -3, bullet));
                    bullets.add(super.shoot(42, -12, 2, -3, bullet));
                    break;
                case 6:
                    if (moveCount - lastShotMoveCount < 30)
                        return bullets;
                case 7:
                case 8:
                    if (moveCount - lastShotMoveCount < 20)
                        return bullets;
                    bullet.setAppearance(bullet2Appearance);
                    bullet.setCollisionBox(new CollisionBox(new Circle[]{
                            new Circle(7, 0, 0)
                    }));
                    Point diff = (Point) player.getPos().clone();
                    diff.translate(-getPos().x,getPos().y);
                    bullets.add(super.shoot(-42, -12, 5 * ((double) diff.x / diff.y), -5, bullet));
                    bullets.add(super.shoot(42, -12, 5 * ((double) diff.x / diff.y), -5, bullet));
                    break;
            }
        }
        lastShotMoveCount = moveCount;
        ++shootCount;
        return bullets;
    }

}
