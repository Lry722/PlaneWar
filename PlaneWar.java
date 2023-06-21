import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;

public class PlaneWar extends JFrame implements ActionListener {
    private static BufferedImage background;
    private static BufferedImage icon;
    private static Clip backgroundMusic;

    static {
        try {
            background = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("image/background.png"));
            icon = ImageIO.read(PlaneWar.class.getClassLoader().getResourceAsStream("icon/icon.png"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(AudioSystem.getAudioInputStream(new BufferedInputStream(PlaneWar.class.getClassLoader().getResourceAsStream("sound/music.wav"))));
            FloatControl volume = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-5f);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.out.println("加载PlaneWar资源时出错");
            e.printStackTrace();
        }
    }

    private final Point gameSize = new Point(background.getWidth(), background.getHeight());
    private final GameProcessController controller = new GameProcessController(gameSize);
    private final InputController inputController = new InputController();
    private final UI ui = new UI();

    public PlaneWar() {
        super("飞机大战");
        setIconImage(icon);
        setSize(background.getWidth(), background.getHeight());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(inputController);
        backgroundMusic.start();
        Timer timer = new Timer(15, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Timer) {
            if (inputController.isGameStart() && controller.getGameState() != GameProcessController.GameState.INGAME)
                controller.init();
            controller.setPlayerVelocity(inputController.getVx(), inputController.getVy());
            if (inputController.isUseBomb())
                controller.useBomb();
            if (inputController.isPause())
                controller.pause();
            controller.update();
            if (!controller.isPaused())
                repaint();
        }
    }

    @Override
    public void paint(Graphics g) {
        //创建缓冲image
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics imageGraphics = image.getGraphics();
        //绘制滚动背景
        imageGraphics.drawImage(background, 0, (controller.getFrameCount() % gameSize.y) - gameSize.y, null);
        imageGraphics.drawImage(background, 0, controller.getFrameCount() % gameSize.y, null);
        //绘制所有GameObject
        for (GameObject object :
                controller.getGameObjects()) {
            BufferedImage objectAppearance = object.getAppearance();
            Point objectPos = object.getPos();
            objectPos.translate(-objectAppearance.getWidth() / 2, objectAppearance.getHeight() / 2);
            imageGraphics.drawImage(objectAppearance, objectPos.x, gameSize.y - objectPos.y, null);
        }
        imageGraphics.setColor(Color.WHITE);
        imageGraphics.setFont(new Font("黑体", Font.BOLD, 30));
        if (controller.getGameState() == GameProcessController.GameState.START)
            ui.drawStart(imageGraphics);
        else if (controller.getGameState() == GameProcessController.GameState.INGAME)
            ui.drawInGame(imageGraphics, controller.getPlayerHp(), controller.getScore(), controller.getTimeLeft(), controller.getBombsCount());
        else if (controller.getGameState() == GameProcessController.GameState.WIN)
            ui.drawWin(imageGraphics, controller.getScore());
        else if (controller.getGameState() == GameProcessController.GameState.LOSS)
            ui.drawLoss(imageGraphics, controller.getScore());

        if (controller.isBossOnStage())
            ui.drawBossHP(imageGraphics, controller.getBossHp());
        //将缓冲的image绘制到窗口上
        g.drawImage(image, 0, 0, null);
    }
}
