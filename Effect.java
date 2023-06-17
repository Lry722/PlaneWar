import java.awt.image.BufferedImage;

public class Effect extends GameObject{
    private BufferedImage [] frames;
    private int cur = 0;
    private boolean loop = false;
    protected int nextCalledCount = 0;

    public Effect(BufferedImage [] frames) {
        this.frames = frames;
    }

    @Override
    public BufferedImage getAppearance() {
        return next();
    }

    public BufferedImage current() {
        if (cur == frames.length)
                throw new IndexOutOfBoundsException("next在Effect播放已结束时被调用");
        return frames[cur];
    }

    public BufferedImage next() {
        BufferedImage back = current();
        ++cur;
        if (loop && cur == frames.length)
            cur = 0;

        return back;
    }
    public final boolean hasNext() {
        return cur < frames.length;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }
}
