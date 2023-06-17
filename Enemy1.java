import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Enemy1 extends Shooter {
    private static BufferedImage appearance;
    private static BufferedImage bulletAppearance;
    private static Clip downSound;

    static {
        try {
            appearance = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/enemy1.png"));
            bulletAppearance = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/enemy bullet 1.png"));
            downSound = AudioSystem.getClip();
            downSound.open(AudioSystem.getAudioInputStream(new BufferedInputStream(PlaneWar.class.getClassLoader().getResourceAsStream("sound/enemy down.wav"))));
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("加载Enemy1资源时出错");
            e.printStackTrace();
        }
    }

    @Override
    public void hit(int damage) {
        super.hit(damage);
        if (getHP() == 0) {
            downSound.setFramePosition(0);
            downSound.start();
        }
    }

    public Enemy1() {
        setAppearance(appearance);
        setCollisionBox(new CollisionBox(new Circle[]{
                new Circle(18, 0, 5),
                new Circle(7, 0, -20),
                new Circle(13, 29, -5),
                new Circle(13, -29, -5)
        }));
        setHP(100);
    }

    public ArrayList<Bullet> shoot() {
        ArrayList<Bullet> bullets = new ArrayList<>();
        if (moveCount - lastShotMoveCount < 120)
            return bullets;

        Bullet bullet = new Bullet();
        bullet.setAppearance(bulletAppearance);
        bullet.setTarget(Bullet.Target.PLAYER);//设定目标
        bullet.setDamage(10);
        bullet.setCollisionBox(new CollisionBox(new Circle[]{
                new Circle(5, 0, -13),
                new Circle(4, 0, -6)
        }));
        //射两列子弹
        bullets.add(super.shoot(-12, -33, 0, -3, bullet));
        bullets.add(super.shoot(12, -33, 0, -3, bullet));
        lastShotMoveCount = moveCount;

        return bullets;
    }
}
