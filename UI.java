
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UI extends JFrame{

    public void drawStart(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("黑体",Font.BOLD,60));
        g.drawString("飞机大战",180,300);
        g.setFont(new Font("黑体",Font.BOLD,40));
        g.drawString("按空格以开始",180,700);
    }


    public void drawInGame(Graphics g,int playerHp,int score,int timeLeft,int bombs) {
        // 设置游戏中界面内容
        g.drawString("剩余血量 "+playerHp,10,900);
        g.drawString("分数 "+score,10,70);
        g.drawString("时间 "+timeLeft,465,70);
        g.drawString("炸弹 "+bombs,480,900);
    }

    public void drawWin(Graphics g,int score) {
        g.setFont(new Font("黑体",Font.BOLD,60));
        g.drawString("胜利",260,390);
        g.drawString("最终分数",195,460);
        g.drawString(new Integer(score).toString(),270,530);
        g.setFont(new Font("黑体",Font.BOLD,40));
        g.drawString("按空格重新开始",170,700);
    }

    public void drawLoss(Graphics g,int score) {
        g.setFont(new Font("黑体",Font.BOLD,60));
        g.drawString("失败",260,390);
        g.drawString("最终分数",195,460);
        g.drawString(new Integer(score).toString(),270,530);
        g.setFont(new Font("黑体",Font.BOLD,40));
        g.drawString("按空格重新开始",170,700);
    }

    public void drawBossHP(Graphics g,int bossHp) {
        System.out.println(bossHp);
        g.setColor(Color.red);
        g.fillRect(100,100,410,20);
        g.setColor(Color.green);
        g.fillRect(100,100,410 * bossHp / 10000,20);
    }
}
