import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ExplosionEffect extends Effect {
    private static BufferedImage[] explosion = new BufferedImage[9];

    static {
        try {
            for (int i = 0; i < 9; ++i)
                explosion[i] = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("effect/explosion/explosion" + (i + 1) + ".png"));
        } catch (IOException e) {
            System.out.println("加载爆炸效果时出错");
            e.printStackTrace();
        }
    }

    @Override
    public BufferedImage next() {
        nextCalledCount++;
        if (nextCalledCount % 3 == 0) {
            return super.next();
        }
        return current();
    }

    public ExplosionEffect() {
        super(explosion);
    }
}
