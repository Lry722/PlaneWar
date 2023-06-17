import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ScoreEffect extends Effect{
    private static BufferedImage [] score = new BufferedImage[3];

    static {
        try {
            for (int i = 0; i < 3; ++i)
                score[i] = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("effect/score/score" + (i + 1) + ".png"));
        } catch (IOException e) {
            System.out.println("加载得分道具效果时出错");
            e.printStackTrace();
        }
    }

    @Override
    public BufferedImage next() {
        nextCalledCount++;
        if (nextCalledCount % 20 == 0) {
            return super.next();
        }
        return current();
    }

    public ScoreEffect() {
        super(score);
        setLoop(true);
    }
}
