import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputController implements KeyListener {
    private boolean gameStart = false;
    private boolean useBomb = false;
    private boolean pause = false;

    public final static int NO_DIRECTION = 0,UP = 0b1000,DOWN = 0b0100,LEFT = 0b0010,RIGHT = 0b0001;

    private int pressedKeys = 0;
    private int currentDirection = NO_DIRECTION;

    public double getVx() {
        if (currentDirection == LEFT)
            return -Player.SPEED;
        else if (currentDirection == RIGHT)
            return Player.SPEED;
        else if ((currentDirection & LEFT) != 0)
            return -Math.sqrt((double) Math.pow(Player.SPEED,2) / 2);
        else if ((currentDirection & RIGHT) != 0)
            return Math.sqrt((double) Math.pow(Player.SPEED,2) / 2);
        else
            return 0;
    }
    public double getVy() {
        if (currentDirection == DOWN)
            return -Player.SPEED;
        else if (currentDirection == UP)
            return Player.SPEED;
        else if ((currentDirection & DOWN) != 0)
            return -Math.sqrt((double) Math.pow(Player.SPEED,2) / 2);
        else if ((currentDirection & UP) != 0)
            return Math.sqrt((double) Math.pow(Player.SPEED,2) / 2);
        else
            return 0;
    }

    public boolean isGameStart() {
        if (gameStart)
        {
            gameStart = false;
            return true;
        }
        else
            return false;
    }

    public boolean isUseBomb() {
        if (useBomb)
        {
            useBomb = false;
            return true;
        }
        else
            return false;
    }

    public boolean isPause() {
        if (pause)
        {
            pause = false;
            return true;
        }
        else
            return false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE)
            gameStart = true;
        else if (key == KeyEvent.VK_NUMPAD0)
            useBomb = true;
        else if (key == KeyEvent.VK_P)
            pause = true;
        else if (key == KeyEvent.VK_W || key == KeyEvent.VK_A || key == KeyEvent.VK_S || key == KeyEvent.VK_D)
        {
            pressedKeys |= 1 << (key - KeyEvent.VK_A);
            switch (key) {
                case KeyEvent.VK_W : currentDirection |= UP;   break;
                case KeyEvent.VK_A : currentDirection |= LEFT;   break;
                case KeyEvent.VK_S : currentDirection |= DOWN;   break;
                case KeyEvent.VK_D : currentDirection |= RIGHT;   break;
                default : break;
            }
            //如果上下或左右冲突，保留新的方向（如原本按着w，新按下s，那当前方向为向下）
            if (((currentDirection & UP) != 0 && (currentDirection & DOWN) != 0) ||
                ((currentDirection & LEFT) != 0 && (currentDirection & RIGHT) != 0))
            {
                switch (key) {
                    case KeyEvent.VK_W : currentDirection &= ~DOWN;   break;
                    case KeyEvent.VK_A : currentDirection &= ~RIGHT;   break;
                    case KeyEvent.VK_S : currentDirection &= ~UP;   break;
                    case KeyEvent.VK_D : currentDirection &= ~LEFT;   break;
                    default : break;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_A || key == KeyEvent.VK_S || key == KeyEvent.VK_D)
        {
            pressedKeys &= ~(1 << (key - KeyEvent.VK_A));
            switch (key) {
                case KeyEvent.VK_W : currentDirection &= ~UP;   break;
                case KeyEvent.VK_A : currentDirection &= ~LEFT;   break;
                case KeyEvent.VK_S : currentDirection &= ~DOWN;   break;
                case KeyEvent.VK_D : currentDirection &= ~RIGHT;   break;
                default : break;
            }
            //如果原本存在上下或左右冲突，松开按键后应视为按下反向按键（如同时按下w和s，松开s后视为上）
            switch (key) {
                case KeyEvent.VK_W : if ((pressedKeys & (1 << (KeyEvent.VK_S - KeyEvent.VK_A))) != 0) currentDirection |= DOWN;   break;
                case KeyEvent.VK_A : if ((pressedKeys & (1 << (KeyEvent.VK_D - KeyEvent.VK_A))) != 0) currentDirection |= RIGHT;   break;
                case KeyEvent.VK_S : if ((pressedKeys & (1 << (KeyEvent.VK_W - KeyEvent.VK_A))) != 0) currentDirection |= UP;   break;
                case KeyEvent.VK_D : if ((pressedKeys & (1 << (KeyEvent.VK_A - KeyEvent.VK_A))) != 0) currentDirection |= LEFT;   break;
                default : break;
            }
        }
    }
}
