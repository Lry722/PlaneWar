import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Player extends Shooter {
    public static final double SPEED = 7;
    private static BufferedImage appearance;
    private static BufferedImage bulletAppearance;
    private static BufferedImage lightAppearance;
    private static BufferedImage shieldAppearance;
    private static Clip bulletSound;
    private static Clip downSound;
    private static Clip bombSound;

    static {
        try {
            appearance = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/player.png"));
            bulletAppearance = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/bullet.png"));
            lightAppearance = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/light.png"));
            shieldAppearance = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/shield.png"));
            bulletSound = AudioSystem.getClip();
            bulletSound.open(AudioSystem.getAudioInputStream(new BufferedInputStream(PlaneWar.class.getClassLoader().getResourceAsStream("sound/bullet.wav"))));
            FloatControl volume = (FloatControl) bulletSound.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-10f);
            downSound = AudioSystem.getClip();
            downSound.open(AudioSystem.getAudioInputStream(new BufferedInputStream(PlaneWar.class.getClassLoader().getResourceAsStream("sound/player down.wav"))));
            bombSound = AudioSystem.getClip();
            bombSound.open(AudioSystem.getAudioInputStream(new BufferedInputStream(PlaneWar.class.getClassLoader().getResourceAsStream("sound/use bomb.wav"))));
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            System.out.println("加载Player资源时出错");
            e.printStackTrace();
        }
    }

    private boolean visible = false;
    private int level = 1;
    private int shield = 0;
    private int bomb = 1;
    private int score = 0;

    public Player() {
        setAppearance(appearance);
        setCollisionBox(new CollisionBox(new Circle[]{
                new Circle(24, 0, 12),
                new Circle(9, 0, 44),
                new Circle(10, -32, 26),
                new Circle(10, 32, 26),
                new Circle(17, -45, 0),
                new Circle(17, 45, 0),
                new Circle(13, 0, -27),
                new Circle(6, -13, -44),
                new Circle(6, 13, -44)
        }));
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    //升一级
    public void levelUp() {
        if (level < 5)
            level += 1;
    }

    //炸弹+1
    public void getBomb() {
        if (bomb < 3)
            bomb += 1;
    }

    public boolean bomb()
    {
        if (bomb > 0) {
            bombSound.setFramePosition(0);
            bombSound.start();
            bomb -= 1;
            return true;
        }
        else
            return false;
    }

    public void addScore(int bonus) {
        score += bonus;
    }

    public int getScore() {
        return score;
    }

    @Override
    public void init() {
        super.init();
        level = 1;
        shield = 0;
        bomb = 1;
        score = 0;
        setHP(200);
        visible = true;
        lastShootMoveCount = -1000;
    }

    @Override
    public void hit(int damage) {
        //若有护盾则消耗护盾次数
        if (shield > 0)
            --shield;
        else
            super.hit(damage);
        //血量归0时播放失败音效
        if (getHP() == 0) {
            downSound.setFramePosition(0);
            downSound.start();
        }
    }

    public void resetShield() {
        shield = 3;
    }

    @Override
    public BufferedImage getAppearance() {
        BufferedImage appearance = new BufferedImage(127, 127, BufferedImage.TYPE_INT_ARGB);
        if (!visible)
            return appearance;
        Graphics graphics = appearance.getGraphics();
        graphics.drawImage(super.getAppearance(), 0, 0, null);
        if (shield > 0)
            graphics.drawImage(shieldAppearance, 0, 0, null);

        return appearance;
    }

    @Override
    public ArrayList<Bullet> shoot() {
        ArrayList<Bullet> bullets = new ArrayList<>();
        if (level >= 5)
            shootInterval = 20;
        else if (level >= 4)
            shootInterval = 30;
        else
            shootInterval = 40;

        if (moveCount - lastShootMoveCount < shootInterval)
            return bullets;

        Bullet bullet = new Bullet();
        bullet.setAppearance(bulletAppearance);
        bullet.setTarget(Bullet.Target.ENEMY);//设定目标
        bullet.setDamage(40);
        bullet.setCollisionBox(new CollisionBox(new Circle[]{
                new Circle(5, 0, 13),
                new Circle(4, 0, 6)
        }));
        if (level >= 1) {
            bullets.add(super.shoot(0, 55, 0, 8, bullet));
        }
        if (level >= 2) {
            bullets.add(super.shoot(33, 33, 0, 8, bullet));
            bullets.add(super.shoot(-33, 33, 0, 8, bullet));
        }
        //每发射2次，发射1次激光
        if (level >= 3 && this.shootCount % 2 == 0) {
            Bullet light = new Bullet();
            Circle[] circles = new Circle[9];
            for (int i = 0; i < circles.length; i++)
                circles[i] = new Circle(4, 0, 40 - 8 * i);
            light.setCollisionBox(new CollisionBox(circles));
            light.setAppearance(lightAppearance);
            light.setTarget(Bullet.Target.ENEMY);
            light.setDamage(60);
            bullets.add(super.shoot(53, 60, 0, 12, light.clone()));
            bullets.add(super.shoot(-53, 60, 0, 12, light.clone()));
        }
        lastShootMoveCount = moveCount;
        shootCount += 1; //发射计数

        bulletSound.setFramePosition(0);
        bulletSound.start();

        return bullets;
    }
}
