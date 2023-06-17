public class Bullet extends GameObject {
    private int damage; //伤害

    enum Target {PLAYER, ENEMY} //目标

    Target target;

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    public void setTarget(Target t) {
        this.target = t;
    }

    public Target getTarget() {
        return target;
    }

    @Override
    public Bullet clone() {
        Bullet newBullet = null;
        try {
            newBullet = (Bullet) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return newBullet;
    }
}
