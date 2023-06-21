import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Random;

public class Item extends GameObject {
    private static BufferedImage levelUpAppearance;
    private static BufferedImage shieldAppearance;
    private static BufferedImage bombAppearance;
    private static Clip levelUpSound;
    private static Clip shieldSound;
    private static Clip getBombSound;

    private ScoreEffect scoreEffect = new ScoreEffect();

    static {
        try {
            levelUpAppearance = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/item level up.png"));
            shieldAppearance = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/item shield.png"));
            bombAppearance = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/item bomb.png"));
            levelUpSound = AudioSystem.getClip();
            levelUpSound.open(AudioSystem.getAudioInputStream(new BufferedInputStream(PlaneWar.class.getClassLoader().getResourceAsStream("sound/upgrade.wav"))));
            shieldSound = AudioSystem.getClip();
            shieldSound.open(AudioSystem.getAudioInputStream(new BufferedInputStream(PlaneWar.class.getClassLoader().getResourceAsStream("sound/supply.wav"))));
            getBombSound = AudioSystem.getClip();
            getBombSound.open(AudioSystem.getAudioInputStream(new BufferedInputStream(PlaneWar.class.getClassLoader().getResourceAsStream("sound/get bomb.wav"))));
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("加载道具资源时出错");
            e.printStackTrace();
        }
    }

    public enum EFFECT {LEVEL_UP, SHIELD, BOMB, SCORE}

    EFFECT effect;

    public static Item generateItem() {
        double rd = new Random().nextDouble();
        Item newItem = null;
        if (rd < 0.1) {
            newItem = new Item(EFFECT.SHIELD);
            System.out.println("生成护盾道具");
        } else if (rd < 0.2) {
            newItem = new Item(EFFECT.LEVEL_UP);
            System.out.println("生成升级道具");
        } else if (rd < 0.3) {
            newItem = new Item(EFFECT.BOMB);
            System.out.println("生成炸弹道具");
        } else if (rd < 0.5) {
            newItem = new Item(EFFECT.SCORE);
            System.out.println("生成得分道具");
        }
        return newItem;
    }

    @Override
    public BufferedImage getAppearance() {
        switch (effect) {
            case LEVEL_UP:
                return levelUpAppearance;
            case SHIELD:
                return shieldAppearance;
            case BOMB:
                return bombAppearance;
            case SCORE:
                return scoreEffect.next();
            default:
                System.out.println(effect);
                return null;
        }
    }

    public Item(EFFECT effect) {
        this.effect = effect;
        setCollisionBox(new CollisionBox(new Circle[]{
                new Circle(28, 0, 0)
        }));
    }

    public void apply(Player object) {
        switch (effect) {
            case LEVEL_UP:
                object.levelUp();
                levelUpSound.setFramePosition(0);
                levelUpSound.start();
                break;
            case SHIELD:
                object.resetShield();
                shieldSound.setFramePosition(0);
                shieldSound.start();
                break;
            case BOMB:
                object.getBomb();
                getBombSound.setFramePosition(0);
                getBombSound.start();
                break;
            case SCORE:
                object.addScore(5);
                break;
            default:
        }
    }
}
