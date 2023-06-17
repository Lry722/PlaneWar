import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;

public abstract class Shooter extends GameObject {
    private int HP = 0; //血量
    private boolean attacked = false; //记录受到攻击，用于对外观进行闪烁处理
    private long time; //记录受击时间，200毫秒后清除受击记录
    protected int shootCount = 0; //射击计数
    protected int lastShotMoveCount = -1000;

    public void init() {
        super.init();
        HP = 0;
        attacked = false;
        shootCount = 0;
        lastShotMoveCount = -1000;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    //受到damage点伤害，并记录受击时间
    public void hit(int damage) {
        time = System.currentTimeMillis();
        attacked = true;
        HP -= damage;
        if (HP < 0) {
            HP = 0;
        }
    }

    public BufferedImage getAppearance() {
        BufferedImage appearance = super.getAppearance();
        //attacked为假则直接返回储存的外观
        if (!attacked) {
            return appearance;
        }
        //attacked为真则继续以下步骤
        //检测100毫秒是否已过，已过则将attacked置否并返回储存的外观
        if (System.currentTimeMillis() - time > 100) {
            attacked = false;
            return appearance;
        }
        //执行到这说明100毫秒内受到过攻击，则对新外观进行增亮处理，实现闪烁效果
        BufferedImage newAppearance = new BufferedImage(appearance.getWidth(), appearance.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = newAppearance.getGraphics();
        graphics.drawImage(appearance, 0, 0, null);
        for (int i = 0; i < newAppearance.getWidth(); i++) {
            for (int j = 0; j < newAppearance.getHeight(); j++) {
                int pixel = newAppearance.getRGB(i, j);
                int alpha = (pixel >> 24) & 0xff;
                if (alpha == 0)
                    continue;
                Color color = new Color(pixel);
                newAppearance.setRGB(i, j, color.brighter().getRGB() & (alpha << 24 | 0xFFFFFF));
            }
        }
        return newAppearance;
    }

    public int getHP() {
        return HP;
    }

    //以参数中的bullet为模板克隆一份bullet，以自身坐标为偏移量，给bullet设定正确的坐标和速度
    protected final Bullet shoot(int x, int y, double vx, double vy, Bullet bullet) {
        Bullet newBullet = bullet.clone();
        Point bulletPos = getPos();
        bulletPos.translate(x,y);
        newBullet.setPos(bulletPos);
        newBullet.setVelocity(vx, vy);

        return newBullet;
    }

    public abstract ArrayList<Bullet> shoot();
}