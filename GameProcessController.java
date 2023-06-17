import javax.sound.sampled.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.max;

public class GameProcessController {
    private static Clip bossSound;

    static {
        try {
            bossSound = AudioSystem.getClip();
            bossSound.open(AudioSystem.getAudioInputStream(new BufferedInputStream(PlaneWar.class.getClassLoader().getResourceAsStream("sound/boss.wav"))));
            FloatControl volume = (FloatControl) bossSound.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(6);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            System.out.println("加载boss音效时出错");
            e.printStackTrace();
        }
    }

    enum GameState {START, INGAME, WIN, LOSS}

    private final Point gameSize;
    private GameState gameState = GameState.START;
    private int frameCount = 0;
    private int lastEnemyFrame = -1000;
    private boolean paused = false;
    private long pausedTime = 0;

    private long beginTime = 0;
    private boolean bossOnStage = false;

    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<Shooter> enemies = new ArrayList<>();
    private final ArrayList<Item> items = new ArrayList<>();
    private final ArrayList<Effect> effects = new ArrayList<>();
    private final Player player = new Player();
    private final Boss boss = new Boss(player);

    public GameProcessController(Point gameSize) {
        this.gameSize = gameSize;
    }

    private void clear() {
        bullets.clear();
        enemies.clear();
        items.clear();
        effects.clear();
    }

    public void init() {
        clear();
        gameState = GameState.INGAME;
        frameCount = 0;
        lastEnemyFrame = -1000;
        paused = false;
        beginTime = System.currentTimeMillis();
        bossOnStage = false;
        player.init();
        player.setPos(gameSize.x / 2, 100);
        player.setVelocity(0, 0);
        boss.init();
        boss.setPos(gameSize.x / 2,gameSize.y);
    }

    public void setPlayerVelocity(double vx,double vy) {
        player.setVelocity(vx,vy);
    }

    public boolean isBossOnStage() {
        return bossOnStage;
    }

    //使用炸弹
    public void useBomb()
    {
        if (!player.bomb())
            return;
        for (Shooter enemy : enemies) {
            enemy.hit(100);
        }
        for (int i = 0;i < bullets.size();++i) {
            if (bullets.get(i).getTarget() == Bullet.Target.PLAYER)
                bullets.remove(i--);
        }
    }

    public void update() {
        if (getTimeLeft() <= 0) {
            clear();
            gameState = GameState.LOSS;
        }
        if (paused || gameState == GameState.LOSS)
            return;

        //移动所有Enemy,Bullet,Item一次,若enemy移动后x < 0 或 x > gameSize.x则反转水平速度，所有对象移动后若y < 0或y > gameSize.y则移除该对象
        for (int i = 0; i < enemies.size(); ++i) {
            Shooter enemy = enemies.get(i);
            enemy.move();
            if (enemy.getPos().x < 0 || enemy.getPos().x > gameSize.x)
                enemy.setVelocity(-enemy.getVx(),enemy.getVy());
            if (enemy.getPos().y < 0 || enemy.getPos().y > gameSize.y)
                enemies.remove(i--);
        }
        for (int i = 0; i < bullets.size(); ++i) {
            bullets.get(i).move();
            if (bullets.get(i).getPos().y < 0 || bullets.get(i).getPos().y > gameSize.y)
                bullets.remove(i--);
        }
        for (int i = 0; i < items.size(); ++i) {
            items.get(i).move();
            if (items.get(i).getPos().y < 0 || items.get(i).getPos().y > gameSize.y)
                items.remove(i--);
        }
        //移动玩家一次，出界了则移回来
        player.move();
        Point playerPos = player.getPos();
        if (playerPos.x < 0 || playerPos.x > gameSize.x || playerPos.y < 0 || playerPos.y > gameSize.y)
            player.moveBack();

        //检测玩家和物品碰撞
        for (int i = 0; i < items.size(); ++i) {
            Item item = items.get(i);
            if (item.collideWith(player)) {
                item.apply(player);
                items.remove(i--);
            }
        }
        //检测子弹命中
        for (int i = 0; i < bullets.size(); ++i) {
            Bullet bullet = bullets.get(i);
            if (bullet.getTarget() == Bullet.Target.PLAYER) {
                if (bullet.collideWith(player)) {
                    System.out.println("子弹命中玩家");
                    player.hit(bullet.getDamage());
                    bullets.remove(i--);
                }
            } else {
                for (Shooter enemy : enemies) {
                    if (bullet.collideWith(enemy)) {
                        System.out.println("子弹命中敌机");
                        enemy.hit(bullet.getDamage());
                        bullets.remove(bullet);
                        break;
                    }
                }
            }
        }
        //检测玩家和敌机碰撞
        for (Shooter enemy : enemies) {
            if (enemy.collideWith(player)) {
                System.out.println("玩家和敌机碰撞");
                player.hit(50);
                enemy.hit(50);
                break;
            }
        }

        //检测玩家血量，判断游戏是否已结束
        if (player.getHP() == 0) {
            gameState = GameState.LOSS;
            player.setVisible(false);
            clear();
            System.out.println("游戏失败");
            return;
        }

        //检测被击毁的敌机，添加爆炸效果，加分并随机生成道具
        for (int i = 0; i < enemies.size(); ++i) {
            Shooter enemy = enemies.get(i);
            if (enemy.getHP() == 0) {
                System.out.println("Enemy销毁");
                player.addScore(10);
                if (enemy == boss)
                {
                    player.addScore(90);
                    player.addScore(getTimeLeft());
                    player.setVisible(false);
                    gameState = GameState.WIN;
                    clear();
                    System.out.println("游戏胜利");
                    return;
                }
                //添加爆炸效果
                ExplosionEffect newEffect = new ExplosionEffect();
                newEffect.setPos(enemy.getPos());
                effects.add(newEffect);
                //生成道具
                Item newItem = Item.generateItem();
                if (newItem != null) {
                    newItem.setPos(enemy.getPos());
                    newItem.setVelocity(0, -3);
                    items.add(newItem);
                }
                enemies.remove(i--);
            }
        }

        //清除播放完的effect
        for (int i = 0;i < effects.size();++i)
        {
            if (!effects.get(i).hasNext())
                effects.remove(i--);
        }

        //射击
        for (Shooter enemy :
                enemies)
            bullets.addAll(enemy.shoot());
        bullets.addAll(player.shoot());

        //时间到了就让boss出现
        if (!bossOnStage && getTimeLeft() <= 60) {
            bossSound.setFramePosition(0);
            bossSound.start();
            enemies.add(boss);
            bossOnStage = true;
        }

        //当没boss时才刷怪，刷怪速度随时间递增,初始时为每隔210帧出一个，每过1秒该间隔减2帧,最低为20帧
        Random random = new Random();
        if (!bossOnStage && frameCount - lastEnemyFrame > max((210 - frameCount / 30),20)) {
            lastEnemyFrame = frameCount;
            Shooter newEnemy;
            if (random.nextBoolean()) {
                newEnemy = new Enemy1();
                System.out.println("添加Enemy1");
            } else {
                newEnemy = new Enemy2();
                System.out.println("添加Enemy2");
            }
            newEnemy.setPos(random.nextDouble() * gameSize.x, gameSize.y);
            newEnemy.setVelocity(random.nextDouble() * 6 - 3, -2);
            enemies.add(newEnemy);
        }
        ++frameCount;
    }

    public ArrayList<GameObject> getGameObjects() {
        ArrayList<GameObject> objects = new ArrayList<>();
        objects.addAll(items);
        objects.addAll(bullets);
        objects.addAll(enemies);
        objects.addAll(effects);
        objects.add(player);

        return objects;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void pause() {
        if (!paused)
        {
            paused = true;
            pausedTime = System.currentTimeMillis();
        } else {
            paused = false;
            beginTime += System.currentTimeMillis() - pausedTime;
        }
    }

    public int getTimePassed() {
        return (int) (System.currentTimeMillis() - beginTime) / 1000;
    }

    public int getTimeLeft() {
        final int timeLimit = 180;
        return timeLimit - getTimePassed();
    }

    public int getScore() {
        return player.getScore();
    }
}
