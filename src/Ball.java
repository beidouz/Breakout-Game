import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Ball {
    private Point location;
    private int width;
    private int height;
    private int dx;
    private int dy;
    public boolean launched;
    public final int defaultBallWidth = 12;
    private BufferedImage icon;

    public Ball(Point location, int width, int speed) {
        this.location = location; // TODO change the default location
        this.width = width;
        this.height = width;
        this.dx = speed;  // TODO use speed arg
        this.dy = speed;
        this.launched = false;
        try {
            this.icon = ImageIO.read(new File("src/icons/ball.png"));
        } catch (IOException e) {
            System.out.println("Exception: failed to read in ball icon, make sure all 5 icon files exist");
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public int left() {
        return this.location.x;
    }

    public int right() {
        return this.location.x + this.width;
    }

    public int top() {
        return this.location.y;
    }

    public int bottom() {
        return this.location.y + this.width;
    }

    public int getDx() {
        return this.dx;
    }

    public int getDy() {
        return this.dy;
    }

    public void setVelocity(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public boolean isLaunched() {
        return this.launched;
    }

    public void launch(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
        this.launched = true;
    }

    protected void draw(Graphics2D g2, Dimension windowSize, Dimension defaultWindowSize) {
        g2.setColor(Color.gray);
        Double h = this.defaultBallWidth * windowSize.getHeight() / defaultWindowSize.getHeight();
        this.height = h.intValue();
        this.width = h.intValue();
        g2.drawImage(this.icon, this.location.x, this.location.y, h.intValue(), h.intValue(), null);
    }

}
